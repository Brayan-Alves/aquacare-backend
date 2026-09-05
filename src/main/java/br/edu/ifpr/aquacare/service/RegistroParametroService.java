package br.edu.ifpr.aquacare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.TipoAgua;
import br.edu.ifpr.aquacare.repository.RegistroParametroRepository;
import jakarta.persistence.EntityNotFoundException;

@Service 
public class RegistroParametroService {
    
    final private RegistroParametroRepository registroParametroRepository;

    public RegistroParametroService(RegistroParametroRepository registroParametroRepository){
        this.registroParametroRepository = registroParametroRepository;
    }

    public RegistroParametro cadastrar(RegistroParametro registro){
        if(registro.getAquario().getTipoAgua() == TipoAgua.MARINHO && registro.getSalinidade() == null){
            throw new IllegalArgumentException("Salinidade é obrigatória para o registro de parâmetros em aquários marinhos.");
        }
        return registroParametroRepository.save(registro);
    }

    public RegistroParametro buscarPorId(int id){
        return  registroParametroRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Registro não encontrado."));
    }

    public List<RegistroParametro> buscarPorIdAquario(int idAquario){
        return registroParametroRepository.findByAquarioId(idAquario);
    }

    public RegistroParametro atualizar(int id, RegistroParametro dadosAtualizados){
        RegistroParametro registro = buscarPorId(id);

        registro.setDataHora(dadosAtualizados.getDataHora());
        registro.setPh(dadosAtualizados.getPh());
        registro.setTemperatura(dadosAtualizados.getTemperatura());
        registro.setAmonia(dadosAtualizados.getAmonia());
        registro.setNitrito(dadosAtualizados.getNitrito());
        registro.setNitrato(dadosAtualizados.getNitrato());
        registro.setGh(dadosAtualizados.getGh());
        registro.setKh(dadosAtualizados.getKh());
        registro.setCo2(dadosAtualizados.getCo2());
        registro.setSalinidade(dadosAtualizados.getSalinidade());
        registro.setCalcio(dadosAtualizados.getCalcio());
        registro.setMagnesio(dadosAtualizados.getMagnesio());

        if(registro.getAquario().getTipoAgua() == TipoAgua.MARINHO && registro.getSalinidade() == null){
            throw new IllegalArgumentException("Salinidade é obrigatória para o registro de parâmetros em aquários marinhos.");
        }

        return registroParametroRepository.save(registro);
    }

    public void excluir(int id){
        registroParametroRepository.delete(buscarPorId(id));
    }


}
