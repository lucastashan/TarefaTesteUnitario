package com.lucastashan;

public class CentroDistribuicao {
  public enum SITUACAO { NORMAL, SOBRAVISO, EMERGENCIA }
  public enum TIPOPOSTO { COMUM, ESTRATEGICO }

  public static final int MAX_ADITIVO = 500;
  public static final int MAX_ALCOOL = 2500;
  public static final int MAX_GASOLINA = 10000;

  // tanques e situação
  private int tAditivo;
  private int tGasolina;
  private int tAlcool1;
  private int tAlcool2;
  private SITUACAO situacao;

  public CentroDistribuicao(int tAditivo, int tGasolina, int tAlcool1, int tAlcool2) throws IllegalArgumentException{ 
    if( tAditivo < 0 || tGasolina < 0 || tAlcool1 < 0 || tAlcool2 < 0 || tAlcool1 != tAlcool2 ) {
      throw new IllegalArgumentException("ILLEGAL_ARGUMENT_EXCEPTION");
    }
    if(tAditivo > MAX_ADITIVO) this.tAditivo = MAX_ADITIVO;
    else this.tAditivo = tAditivo;

    if(tGasolina > MAX_GASOLINA) this.tGasolina = MAX_GASOLINA;
    else this.tGasolina = tGasolina;

    if(tAlcool1 + tAlcool2 > MAX_ALCOOL){
      this.tAlcool1 = MAX_ALCOOL / 2;
      this.tAlcool2 = MAX_ALCOOL / 2;
    } else{
      this.tAlcool1 = tAlcool1;
      this.tAlcool2 = tAlcool2;
    }

    defineSituacao();
  }

  public void defineSituacao(){
    if(tGasolina >= (MAX_GASOLINA/2) && tAditivo >= (MAX_ADITIVO/2) && (tAlcool1+tAlcool2) >= (MAX_ALCOOL/2)){
      situacao = SITUACAO.NORMAL;
    }
    else if(tGasolina >= (MAX_GASOLINA/4) && tAditivo >= (MAX_ADITIVO/4) && (tAlcool1+tAlcool2) >= (MAX_ALCOOL/4)){
      situacao = SITUACAO.SOBRAVISO;
    }
    else situacao = SITUACAO.EMERGENCIA;
  }

  public SITUACAO getSituacao(){
    return situacao;
  }

  public int gettGasolina(){
    return tGasolina;
  }

  public int gettAditivo(){
    return tAditivo;
  }

  public int gettAlcool1(){
    return tAlcool1;
  }

  public int gettAlcool2(){
    return tAlcool2;
  }

  public int recebeAditivo(int qtdade) {
    if(qtdade < 0) return -1;
    if(qtdade+tAditivo > MAX_ADITIVO){
      int aux = tAditivo;
      tAditivo = MAX_ADITIVO;
      return (tAditivo - aux);
    }
    tAditivo+= qtdade;
    return qtdade;
  }

  public int recebeGasolina(int qtdade) {
    if(qtdade < 0) return -1;
    if(qtdade+tGasolina > MAX_GASOLINA){
      int aux = tGasolina;
      tGasolina = MAX_GASOLINA;
      return (tGasolina - aux);
    }
    tGasolina+= qtdade;
    return qtdade;
  }

  public int recebeAlcool(int qtdade) {
    if(qtdade < 0) return -1;
    if(qtdade+tAlcool1+tAlcool2 > MAX_ALCOOL){
      int aux = tAlcool1 + tAlcool2;
      tAlcool1 = MAX_ALCOOL / 2;
      tAlcool2 = MAX_ALCOOL / 2;
      return ((tAlcool1+tAlcool2) - aux);
    }
    tAlcool1 = tAlcool1 + (qtdade/2);
    tAlcool2 = tAlcool2 + (qtdade/2);
    return qtdade;
  }

  public int[] encomendaCombustivel(int qtdade, TIPOPOSTO tipoPosto) {
    if(qtdade < 0) return new int[] {-7,0,0,0};

    double gasolina = qtdade * 0.7;
    double aditivo = qtdade * 0.05;
    double alcool1 = qtdade * 0.25 * 0.5;
    double alcool2 = qtdade * 0.25 * 0.5;

    //Se a situacao for normal ou a situacao for sobreaviso e o posto estrategico(tem o mesmo comportamento)
    if(getSituacao() == SITUACAO.NORMAL || (getSituacao() == SITUACAO.SOBRAVISO && tipoPosto == TIPOPOSTO.ESTRATEGICO)) {
      if( tGasolina >= gasolina && tAditivo >= aditivo && tAlcool1 >= alcool1 && tAlcool2 >= alcool2 ){
        tGasolina-= gasolina;
        tAditivo-= aditivo;
        tAlcool1-= alcool1;
        tAlcool2-= alcool2;
        return new int[] {(int)aditivo, (int)gasolina, (int)alcool1, (int)alcool2};
      } else return new int[] {-21,0,0,0};
    }

    if(getSituacao() == SITUACAO.SOBRAVISO){
      if( tGasolina >= gasolina && tAditivo >= aditivo && tAlcool1 >= alcool1 && tAlcool2 >= alcool2 ){
        if(tipoPosto == TIPOPOSTO.COMUM){
          tGasolina-= (gasolina/2);
          tAditivo-= (aditivo/2);
          tAlcool1-= (alcool1/2);
          tAlcool2-= (alcool2/2);
          return new int[] {(int)(aditivo/2), (int)(gasolina/2), (int)(alcool1/2), (int)(alcool2/2)};
        }
      } else return new int[] {-21,0,0,0};
    }

    if(tipoPosto == TIPOPOSTO.COMUM) return new int[] {-14,0,0,0};

    if( tGasolina >= gasolina && tAditivo >= aditivo && tAlcool1 >= alcool1 && tAlcool2 >= alcool2 ){
      tGasolina-= gasolina;
      tAditivo-= aditivo;
      tAlcool1-= alcool1;
      tAlcool2-= alcool2;
      return new int[] {(int)aditivo,(int)gasolina,(int)alcool1,(int)alcool2};
    }
    
    return new int[] {-21,0,0,0};
  }
}
