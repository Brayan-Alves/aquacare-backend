package br.edu.ifpr.aquacare.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.repository.AquarioRepository;

@Service
public class AquarioService {
    
    private final AquarioRepository aquarioRepository;

    public AquarioService(AquarioRepository aquarioRepository){
        this.aquarioRepository = aquarioRepository;
    }

    public Aquario cadastrar(Aquario aquario){
        return aquarioRepository.save(aquario);
    }

    public Aquario buscarPorId(int id){
        return aquarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aquario não encontrado."));
    }

    public List<Aquario> listarTodos(){
        return aquarioRepository.findAll();
    }

    public List<Aquario> listarPorUsuario(String idUsuario){
        return aquarioRepository.findByUsuarioId(idUsuario);
    }

    public Aquario atualizar(int id, Aquario dadosAtualizados){
        Aquario aquario = buscarPorId(id);

        aquario.setNome(dadosAtualizados.getNome());
        aquario.setLitragem(dadosAtualizados.getLitragem());
        aquario.setTipoAgua(dadosAtualizados.getTipoAgua());
        aquario.setNivelPlantas(dadosAtualizados.getNivelPlantas());
        aquario.setCiclado(dadosAtualizados.isCiclado());
        aquario.setInjecaoCO2(dadosAtualizados.isInjecaoCO2());

        return aquarioRepository.save(aquario);
    }

    public void excluir(int id){
        Aquario aquario = buscarPorId(id);
        aquarioRepository.delete(aquario);
        
    }
}
