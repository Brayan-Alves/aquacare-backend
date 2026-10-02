package br.edu.ifpr.aquacare.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.entity.Usuario;
import br.edu.ifpr.aquacare.enums.NivelPlanta;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.service.AquarioService;
import br.edu.ifpr.aquacare.service.DiagnosticoService;
import br.edu.ifpr.aquacare.service.RecomendacaoService;
import br.edu.ifpr.aquacare.service.DiagnosticoService.Diagnostico;
import br.edu.ifpr.aquacare.service.RecomendacaoService.ParametrosIdeais;;

@RestController
@RequestMapping("/aquarios")
public class AquarioController {
    
    private final AquarioService aquarioService;
    private final DiagnosticoService diagnosticoService;
    private final RecomendacaoService recomendacaoService;

    public AquarioController(AquarioService aquarioService, DiagnosticoService diagnosticoService, RecomendacaoService recomendacaoService){
        this.aquarioService = aquarioService;
        this.diagnosticoService = diagnosticoService;
        this.recomendacaoService = recomendacaoService;
    }

    public record DadosRequest(String nome, float litragem, TipoAgua tipoAgua, NivelPlanta nivelPlanta, boolean isCiclado, boolean injecaoCO2){}

    public record AquarioResponse(int id, String nome, float litragem, TipoAgua tipoAgua, NivelPlanta nivelPlantas, LocalDateTime dataMontagem, boolean isCiclado, boolean injecaoCO2){
        public static AquarioResponse from(Aquario aquario){
            return new AquarioResponse(aquario.getId(), aquario.getNome(), aquario.getLitragem(), aquario.getTipoAgua(), aquario.getNivelPlantas(), aquario.getDataMontagem(), aquario.isCiclado(), aquario.isInjecaoCO2());
    }
}


    @PostMapping("/cadastrar")
    public ResponseEntity<Void> cadastrar(@RequestBody DadosRequest dados, @AuthenticationPrincipal Usuario usuario){
        Aquario aquario = new Aquario();

        aquario.setNome(dados.nome());
        aquario.setUsuario(usuario);
        aquario.setLitragem(dados.litragem());
        aquario.setTipoAgua(dados.tipoAgua());
        aquario.setNivelPlantas(dados.nivelPlanta());
        aquario.setCiclado(dados.isCiclado());
        aquario.setInjecaoCO2(dados.injecaoCO2());

        aquarioService.cadastrar(aquario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<AquarioResponse>> listarTodos(){
        List<Aquario> aquarios = aquarioService.listarTodos();
        return ResponseEntity.ok(aquarios.stream().map(AquarioResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AquarioResponse> buscarPorId(@PathVariable  int id){
        Aquario aquario = aquarioService.buscarPorId(id);
        return ResponseEntity.ok(AquarioResponse.from(aquario));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<AquarioResponse>> listarPorUsuario(@AuthenticationPrincipal Usuario usuario){
        List<Aquario> aquarios = aquarioService.listarPorUsuario(usuario.getId());
        return ResponseEntity.ok(aquarios.stream().map(AquarioResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@RequestBody DadosRequest dados, @PathVariable  int id){
        Aquario aquario = new Aquario();

        aquario.setNome(dados.nome());
        aquario.setLitragem(dados.litragem());
        aquario.setTipoAgua(dados.tipoAgua());
        aquario.setNivelPlantas(dados.nivelPlanta());
        aquario.setCiclado(dados.isCiclado());
        aquario.setInjecaoCO2(dados.injecaoCO2());

        aquarioService.atualizar(id, aquario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id){
        aquarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/diagnostico")
    public ResponseEntity<Diagnostico> diagnosticar(@PathVariable int id){
        Diagnostico diagnostico = diagnosticoService.diagnosticar(id);

        return ResponseEntity.ok(diagnostico);
    }

    @GetMapping("/{id}/parametros-ideais") 
    public ResponseEntity<ParametrosIdeais> recomendarParametros(@PathVariable int id){
        ParametrosIdeais parametros = recomendacaoService.sugerirParametrosIdeais(id);

        return ResponseEntity.ok(parametros);
    }
    
    @GetMapping("/{id}/especies-recomendadas")
    public ResponseEntity<List<Animal>> recomendarEspecies(@PathVariable int id){
        List<Animal> animaisRecomendados =  recomendacaoService.sugerirEspecies(id);

        return ResponseEntity.ok(animaisRecomendados);
    }

}
