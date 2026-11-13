package br.edu.ifpr.aquacare.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.aquacare.entity.Aquario;
import br.edu.ifpr.aquacare.entity.RegistroParametro;
import br.edu.ifpr.aquacare.enums.TipoAgua;

@Service 
public class ToxicidadeService {

    private static final float SALINIDADE_PADRAO_MARINHA = 35f;

    public float calcularPercentualNH3(RegistroParametro registro){

        float pKa = calcularPKa(registro);
        double expoente = pKa - registro.getPh();
        return (float) (1.0 / (1.0 + Math.pow(10, expoente)));
    }

    private float calcularPKa(RegistroParametro registro){
        Aquario aquario = registro.getAquario();

        if(aquario.getTipoAgua() == TipoAgua.MARINHO){
            float salinidade = registro.getSalinidade() != null ? registro.getSalinidade() : SALINIDADE_PADRAO_MARINHA;
            return 10.0423f - (0.0315536f * registro.getTemperatura()) + (0.003071f * salinidade);
        }

        float tempK = registro.getTemperatura() + 273.15f;
        return 0.09018f + (2729.92f / tempK);
    }

    public float calcularConcentracaoNH3(RegistroParametro registro){
        return  calcularPercentualNH3(registro) * registro.getAmonia();
    }
}