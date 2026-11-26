package br.edu.ifpr.aquacare.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import br.edu.ifpr.aquacare.repository.AquarioRepository;
import br.edu.ifpr.aquacare.repository.PlantaAquarioRepository;

@Service
public class AquarioService {

    private final AquarioRepository aquarioRepository;
    private final AnimalAquarioRepository animalAquarioRepository;
    private final PlantaAquarioRepository plantaAquarioRepository;

    public AquarioService(AquarioRepository aquarioRepository, AnimalAquarioRepository animalAquarioRepository,
            PlantaAquarioRepository plantaAquarioRepository) {
        this.aquarioRepository = aquarioRepository;
        this.animalAquarioRepository = animalAquarioRepository;
        this.plantaAquarioRepository = plantaAquarioRepository;
    }

    public Aquario cadastrar(Aquario aquario) {
        return aquarioRepository.save(aquario);
    }

    public Aquario buscarPorId(int id) {
        return aquarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Aquario não encontrado."));
    }

    public List<Aquario> listarTodos() {
        return aquarioRepository.findAll();
    }

    public List<Aquario> listarPorUsuario(String idUsuario) {
        return aquarioRepository.findByUsuarioId(idUsuario);
    }

    public Aquario atualizar(int id, Aquario dadosAtualizados) {
        Aquario aquario = buscarPorId(id);

        boolean trocouTipoAgua = aquario.getTipoAgua() != dadosAtualizados.getTipoAgua();
        boolean temEspecies = !animalAquarioRepository.findByAquarioId(id).isEmpty() || !plantaAquarioRepository.findByAquarioId(id).isEmpty();
        if (trocouTipoAgua && temEspecies) {
            throw new IllegalArgumentException("Não é possível alterar o tipo de água de um aquário que já possui espécies associadas.");
        }

        aquario.setNome(dadosAtualizados.getNome());
        aquario.setLitragem(dadosAtualizados.getLitragem());
        aquario.setTipoAgua(dadosAtualizados.getTipoAgua());
        aquario.setNivelPlantas(dadosAtualizados.getNivelPlantas());
        aquario.setCiclado(dadosAtualizados.isCiclado());
        aquario.setInjecaoCO2(dadosAtualizados.isInjecaoCO2());

        return aquarioRepository.save(aquario);
    }

    public void excluir(int id) {
        Aquario aquario = buscarPorId(id);
        aquarioRepository.delete(aquario);

    }
}
