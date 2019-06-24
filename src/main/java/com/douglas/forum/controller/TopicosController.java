package com.douglas.forum.controller;

import com.douglas.forum.dto.DetalhesDoTopicoDto;
import com.douglas.forum.dto.TopicoDto;
import com.douglas.forum.form.AtualizacaoTopicoForm;
import com.douglas.forum.form.TopicoForm;
import com.douglas.forum.model.Topico;
import com.douglas.forum.repository.CursoRepository;
import com.douglas.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, @RequestParam int pagina, @RequestParam int qtd){

        Pageable paginacao = PageRequest.of(pagina, qtd);
        if (nomeCurso == null){
            Page<Topico> topicos = topicoRepository.findAll(paginacao);
            return TopicoDto.converter(topicos);
        }else {
            Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
            return TopicoDto.converter(topicos);
        }

    }

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
        Topico topico = form.converter(cursoRepository);
        topicoRepository.save(topico);

        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isPresent()){
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
        }else {
            return  ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
        Optional<Topico> topicoAtualizar = topicoRepository.findById(id);
        if(topicoAtualizar.isPresent()){
           Topico topico = form.atualizar(id, topicoRepository);
           return ResponseEntity.ok(new TopicoDto(topico));
        }else {
            return  ResponseEntity.notFound().build();
        }



    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> remover (@PathVariable Long id) {
        Optional<Topico> topicoRemover = topicoRepository.findById(id);
        if (topicoRemover.isPresent()){
            topicoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return  ResponseEntity.notFound().build();
        }


    }

}
