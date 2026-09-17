package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.MesaRequest;
import com.restaurante.RestauranteAPI.dto.response.MesaResponse;
import com.restaurante.RestauranteAPI.services.MesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> buscarTodos(){
        return ResponseEntity.ok(mesaService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(mesaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MesaResponse> salvar(@Valid @RequestBody MesaRequest request) {

        MesaResponse mesaSalva = mesaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody MesaRequest request) {
        MesaResponse mesaAtualizada = mesaService.atualizar(id, request);
        return ResponseEntity.ok(mesaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mesaService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}
