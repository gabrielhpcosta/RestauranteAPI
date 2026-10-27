package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.PagamentoMapper;
import com.restaurante.RestauranteAPI.dto.request.EstornarPagamentoRequest;
import com.restaurante.RestauranteAPI.dto.request.PagamentoRequest;
import com.restaurante.RestauranteAPI.dto.response.PagamentoResponse;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.ItemPedido;
import com.restaurante.RestauranteAPI.entities.Pagamento;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.FormaPagamento;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import com.restaurante.RestauranteAPI.enums.StatusPagamento;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.ItemPedidoRepository;
import com.restaurante.RestauranteAPI.repositories.PagamentoRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    private static final Long COMANDA_ID = 1L;
    private static final String EMAIL = "garcom@restaurante.com";
    private static final Instant AGORA = Instant.parse("2026-10-27T20:00:00Z");

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private ComandaRepository comandaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    private PagamentoService pagamentoService;

    private Comanda comanda;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Clock fixo: a data do estorno fica previsível e dá para comparar no assert
        Clock clock = Clock.fixed(AGORA, ZoneOffset.UTC);

        // o mapper de verdade é mais simples que mockar toEntity e toResponse.
        // Mappers.getMapper acha a implementação gerada pelo MapStruct sem o teste citar a PagamentoMapperImpl
        PagamentoMapper pagamentoMapper = Mappers.getMapper(PagamentoMapper.class);

        pagamentoService = new PagamentoService(pagamentoRepository, pagamentoMapper,
                comandaRepository, usuarioRepository, itemPedidoRepository, clock);

        comanda = new Comanda();
        comanda.setId(COMANDA_ID);
        comanda.setStatus(StatusComanda.ABERTA);

        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail(EMAIL);
    }

    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("registra o pagamento como CONFIRMADO quando o valor cabe no restante")
        void registraPagamento() {
            prepararComanda();
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.empty());
            when(itemPedidoRepository.findByPedido_Comanda_Id(COMANDA_ID))
                    .thenReturn(List.of(item("50.00", 2, StatusItemPedido.ENTREGUE)));
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID)).thenReturn(List.of());
            when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario));
            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

            PagamentoResponse response = pagamentoService.registrar(request("60.00", "chave-1"), EMAIL);

            ArgumentCaptor<Pagamento> captor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoRepository).save(captor.capture());
            Pagamento salvo = captor.getValue();

            assertThat(salvo.getStatus()).isEqualTo(StatusPagamento.CONFIRMADO);
            assertThat(salvo.getComanda()).isEqualTo(comanda);
            assertThat(salvo.getRegistradoPor()).isEqualTo(usuario);
            assertThat(response.getValor()).isEqualByComparingTo("60.00");
            assertThat(response.getComandaId()).isEqualTo(COMANDA_ID);
        }

        @Test
        @DisplayName("aceita um valor exatamente igual ao restante")
        void aceitaValorIgualAoRestante() {
            prepararComanda();
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.empty());
            when(itemPedidoRepository.findByPedido_Comanda_Id(COMANDA_ID))
                    .thenReturn(List.of(item("50.00", 2, StatusItemPedido.ENTREGUE)));
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID)).thenReturn(List.of());
            when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario));
            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

            PagamentoResponse response = pagamentoService.registrar(request("100.00", "chave-1"), EMAIL);

            assertThat(response.getStatus()).isEqualTo(StatusPagamento.CONFIRMADO);
        }

        @Test
        @DisplayName("recusa um valor acima do restante e não salva nada")
        void recusaValorAcimaDoRestante() {
            prepararComanda();
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.empty());
            when(itemPedidoRepository.findByPedido_Comanda_Id(COMANDA_ID))
                    .thenReturn(List.of(item("50.00", 2, StatusItemPedido.ENTREGUE)));
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID)).thenReturn(List.of());

            assertThatThrownBy(() -> pagamentoService.registrar(request("100.01", "chave-1"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("excede o restante");

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("no restante, ignora itens cancelados e pagamentos estornados")
        void restanteIgnoraCanceladosEEstornados() {
            prepararComanda();
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.empty());
            // consumido: 100 (o item cancelado de 50 não conta)
            when(itemPedidoRepository.findByPedido_Comanda_Id(COMANDA_ID)).thenReturn(List.of(
                    item("50.00", 2, StatusItemPedido.ENTREGUE),
                    item("50.00", 1, StatusItemPedido.CANCELADO)));
            // pago: 30 (o estornado de 20 não conta) -> restante = 70
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID)).thenReturn(List.of(
                    pagamento("30.00", StatusPagamento.CONFIRMADO),
                    pagamento("20.00", StatusPagamento.ESTORNADO)));

            assertThatThrownBy(() -> pagamentoService.registrar(request("70.01", "chave-1"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageEndingWith("R$ 70.00");
        }

        @Test
        @DisplayName("mesma chave na mesma comanda devolve o pagamento existente sem salvar outro")
        void mesmaChaveDevolveExistente() {
            prepararComanda();
            Pagamento existente = pagamento("40.00", StatusPagamento.CONFIRMADO);
            existente.setChaveIdempotencia("chave-1");
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.of(existente));

            PagamentoResponse response = pagamentoService.registrar(request("40.00", "chave-1"), EMAIL);

            assertThat(response.getValor()).isEqualByComparingTo("40.00");
            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("mesma chave devolve o pagamento existente mesmo com a comanda já fechada")
        void mesmaChaveComComandaFechada() {
            comanda.setStatus(StatusComanda.FECHADA);
            prepararComanda();
            Pagamento existente = pagamento("40.00", StatusPagamento.CONFIRMADO);
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.of(existente));

            PagamentoResponse response = pagamentoService.registrar(request("40.00", "chave-1"), EMAIL);

            assertThat(response.getValor()).isEqualByComparingTo("40.00");
            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("recusa uma chave que já foi usada em outra comanda")
        void chaveUsadaEmOutraComanda() {
            prepararComanda();
            Comanda outraComanda = new Comanda();
            outraComanda.setId(99L);
            Pagamento existente = pagamento("40.00", StatusPagamento.CONFIRMADO);
            existente.setComanda(outraComanda);
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> pagamentoService.registrar(request("40.00", "chave-1"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("outra comanda");

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("recusa pagamento em comanda que não está aberta")
        void recusaComandaNaoAberta() {
            comanda.setStatus(StatusComanda.CANCELADA);
            prepararComanda();
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pagamentoService.registrar(request("10.00", "chave-1"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("comandas abertas");

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("lança NotFound quando a comanda não existe")
        void comandaNaoEncontrada() {
            when(comandaRepository.findByIdForUpdate(COMANDA_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pagamentoService.registrar(request("10.00", "chave-1"), EMAIL))
                    .isInstanceOf(NotFound.class);
        }

        @Test
        @DisplayName("busca a comanda com lock (findByIdForUpdate), e não com o findById comum")
        void usaLockNaComanda() {
            prepararComanda();
            Pagamento existente = pagamento("40.00", StatusPagamento.CONFIRMADO);
            when(pagamentoRepository.findByChaveIdempotencia("chave-1")).thenReturn(Optional.of(existente));

            pagamentoService.registrar(request("40.00", "chave-1"), EMAIL);

            verify(comandaRepository).findByIdForUpdate(COMANDA_ID);
            verify(comandaRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("estornar")
    class Estornar {

        @Test
        @DisplayName("estorna um pagamento confirmado e guarda quem, quando e o motivo")
        void estornaPagamento() {
            Pagamento pagamento = pagamento("40.00", StatusPagamento.CONFIRMADO);
            when(pagamentoRepository.findById(5L)).thenReturn(Optional.of(pagamento));
            when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario));
            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

            PagamentoResponse response = pagamentoService.estornar(5L, estorno("Cobrado errado"), EMAIL);

            assertThat(response.getStatus()).isEqualTo(StatusPagamento.ESTORNADO);
            assertThat(response.getEstornadoEm()).isEqualTo(OffsetDateTime.ofInstant(AGORA, ZoneOffset.UTC));
            assertThat(response.getEstornadoPorId()).isEqualTo(usuario.getId());
            assertThat(response.getMotivoEstorno()).isEqualTo("Cobrado errado");
        }

        @Test
        @DisplayName("recusa estornar um pagamento que já foi estornado")
        void recusaPagamentoJaEstornado() {
            Pagamento pagamento = pagamento("40.00", StatusPagamento.ESTORNADO);
            when(pagamentoRepository.findById(5L)).thenReturn(Optional.of(pagamento));

            assertThatThrownBy(() -> pagamentoService.estornar(5L, estorno("motivo"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("confirmados");

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("recusa estornar pagamento de comanda já encerrada")
        void recusaComandaEncerrada() {
            comanda.setStatus(StatusComanda.FECHADA);
            Pagamento pagamento = pagamento("40.00", StatusPagamento.CONFIRMADO);
            when(pagamentoRepository.findById(5L)).thenReturn(Optional.of(pagamento));

            assertThatThrownBy(() -> pagamentoService.estornar(5L, estorno("motivo"), EMAIL))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("encerrada");

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("lança NotFound quando o pagamento não existe")
        void pagamentoNaoEncontrado() {
            when(pagamentoRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pagamentoService.estornar(5L, estorno("motivo"), EMAIL))
                    .isInstanceOf(NotFound.class);
        }
    }

    @Nested
    @DisplayName("existePagamentoConfirmado")
    class ExistePagamentoConfirmado {

        @Test
        @DisplayName("é falso quando a comanda só tem pagamentos estornados")
        void falsoComSoEstornados() {
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID))
                    .thenReturn(List.of(pagamento("20.00", StatusPagamento.ESTORNADO)));

            assertThat(pagamentoService.existePagamentoConfirmado(COMANDA_ID)).isFalse();
        }

        @Test
        @DisplayName("é verdadeiro quando há pelo menos um pagamento confirmado")
        void verdadeiroComConfirmado() {
            when(pagamentoRepository.findByComanda_Id(COMANDA_ID)).thenReturn(List.of(
                    pagamento("20.00", StatusPagamento.ESTORNADO),
                    pagamento("30.00", StatusPagamento.CONFIRMADO)));

            assertThat(pagamentoService.existePagamentoConfirmado(COMANDA_ID)).isTrue();
        }
    }

    // ---------- métodos auxiliares para montar os objetos dos testes ----------

    private void prepararComanda() {
        when(comandaRepository.findByIdForUpdate(COMANDA_ID)).thenReturn(Optional.of(comanda));
    }

    private PagamentoRequest request(String valor, String chave) {
        PagamentoRequest request = new PagamentoRequest();
        request.setComandaId(COMANDA_ID);
        request.setValor(new BigDecimal(valor));
        request.setForma(FormaPagamento.PIX);
        request.setChaveIdempotencia(chave);
        return request;
    }

    private EstornarPagamentoRequest estorno(String motivo) {
        EstornarPagamentoRequest request = new EstornarPagamentoRequest();
        request.setMotivo(motivo);
        return request;
    }

    private ItemPedido item(String precoUnitario, int quantidade, StatusItemPedido status) {
        ItemPedido item = new ItemPedido();
        item.setPrecoUnitario(new BigDecimal(precoUnitario));
        item.setQuantidade(quantidade);
        item.setStatus(status);
        return item;
    }

    private Pagamento pagamento(String valor, StatusPagamento status) {
        Pagamento pagamento = new Pagamento();
        pagamento.setComanda(comanda);
        pagamento.setValor(new BigDecimal(valor));
        pagamento.setStatus(status);
        return pagamento;
    }
}
