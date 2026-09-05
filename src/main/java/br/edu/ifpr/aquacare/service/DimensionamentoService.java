package br.edu.ifpr.aquacare.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Equipamento;

@Service
public class DimensionamentoService {
    private static final float VAZAO_MIN_POR_LITRO = 3f;
    private static final float VAZAO_MAX_POR_LITRO = 5f;
    private static final float POTENCIA_MIN_POR_LITRO = 1f;
    private static final float LUMENS_MIN_POR_LITRO = 30f;
    private static final float LUMENS_MAX_POR_LITRO = 60f;

    public boolean estaDimensionadoCorretamente(Equipamento equipamento){
        float litragem = equipamento.getAquario().getLitragem();

        return switch(equipamento.getTipo()){
            case FILTRO -> vazaoAdequada(equipamento.getVazaoLH(), litragem);
            case TERMOSTATO -> potenciaAdequada(equipamento.getPotenciaWatts(), litragem);
            case ILUMINACAO -> lumensAdequado(equipamento.getLumens(), litragem);
        };
    }



    private boolean potenciaAdequada(float potencia, float litragem){
        if(potencia/litragem >= POTENCIA_MIN_POR_LITRO){
            return  true;
        }
        return false;
    }

    private boolean vazaoAdequada(float vazao, float litragem){
        if(vazao/litragem >= VAZAO_MIN_POR_LITRO && vazao/litragem <= VAZAO_MAX_POR_LITRO){
            return  true;
        }
        return false;
    }

    private boolean lumensAdequado(float lumens, float litragem){
        if(lumens/litragem >= LUMENS_MIN_POR_LITRO && lumens/litragem <= LUMENS_MAX_POR_LITRO){
            return  true;
        }
        return false;
    }



}
