package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.AnimalAquario;
import br.edu.ifpr.aquacare.repository.AnimalAquarioRepository;
import jakarta.persistence.EntityNotFoundException;
import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.entity.Animal;
import br.edu.ifpr.aquacare.enums.TipoAgua;



@Service
public class AnimalAquarioService {

    private final AnimalAquarioRepository animalAquarioRepository;

    public AnimalAquarioService(AnimalAquarioRepository animalAquarioRepository) {
        this.animalAquarioRepository = animalAquarioRepository;
    }

    public AnimalAquario buscarPorId(int id) {
        return animalAquarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Associação entre animal e aquário não encontrada."));
    }

    public AnimalAquario associar(AnimalAquario animalAquario) {
        Aquario aquario = animalAquario.getAquario();
        Animal animal = animalAquario.getAnimal();
        boolean animalDeAguaSalgada = animal.getSalinidadeMin() != null && animal.getSalinidadeMax() != null;

        if (aquario.getTipoAgua() == TipoAgua.MARINHO && !animalDeAguaSalgada) {
            throw new IllegalArgumentException(animal.getNomePopular()
                    + " é uma espécie de água doce e não pode ser adicionada a um aquário marinho.");
        }
        if (aquario.getTipoAgua() == TipoAgua.DOCE && animalDeAguaSalgada) {
            throw new IllegalArgumentException(animal.getNomePopular()
                    + " é uma espécie de água salgada e não pode ser adicionada a um aquário de água doce.");
        }

        return animalAquarioRepository.findByAquarioIdAndAnimalIdAnimal(aquario.getId(), animal.getIdAnimal())
                .map(existente -> {
                    existente.setQuantidade(existente.getQuantidade() + animalAquario.getQuantidade());
                    return animalAquarioRepository.save(existente);
                })
                .orElseGet(() -> animalAquarioRepository.save(animalAquario));
    }

    public List<AnimalAquario> listarPorAquario(int idAquario) {
        return animalAquarioRepository.findByAquarioId(idAquario);
    }

    public AnimalAquario atualizar(int id, int novaQuantidade) {
        AnimalAquario animalAquario = buscarPorId(id);

        animalAquario.setQuantidade(novaQuantidade);

        return animalAquarioRepository.save(animalAquario);
    }

    public void excluir(int id) {
        animalAquarioRepository.delete(buscarPorId(id));
    }
}
