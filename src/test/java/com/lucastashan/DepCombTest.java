package com.lucastashan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DepCombTest {
  private DepComb depComb = null;


  @Test
  public void testeContrutor(){
    depComb = new DepComb(10, 10000, 1250, 1250);
    assertEquals( DepComb.SITUACAO.EMERGENCIA , depComb.getSituacao() );
  }
  
  @BeforeEach
  void setUp(){
    depComb = new DepComb(0,0,0,0);
  }
  
  //TESTE DE PARTICAO E VALOR LIMITE PARA OS METODOS DE RECEBER COMB
  //Testes parametrizados de abastecimento com os tanques vazios
  //Teste de particionamento e valor limite do metodo que abastece o tanque de aditivo
  @ParameterizedTest
  @CsvSource(
    {
      "100,100",
      "500, 500",
      "501, 500"
    }
  )
  public void recebeAditivoTanqVazio(int param, int resEsp){
    assertEquals( resEsp , depComb.recebeAditivo(param) );
  }

  //Teste de particionamento e valor limite do metodo que abastece o tanque de gasolina vazio
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "10000, 10000",
      "10001, 10000"
    }
  )
  public void recebeGasolinaTanqVazio(int param, int resEsp){
    assertEquals( resEsp , depComb.recebeGasolina(param) );
  }

  //Teste de particionamento e valor limite do metodo que abastece os tanques de alcool vazios
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "2500, 2500",
      "2501, 2500"
    }
  )
  public void recebeAlcoolTanqVazio(int param, int resEsp){
    assertEquals( resEsp , depComb.recebeAlcool(param) );
  }

  //Testes parametrizados com os tanques parcialmente cheios
  //Teste de particionamento e valor limite do metodo que abastece o tanque de aditivo parcialmente cheio
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "400, 400",
      "401, 400"
    }
  )
  public void recebeAditivoTanqParcialmenteCheio(int param, int resEsp){
    depComb.recebeAditivo(100);
    assertEquals( resEsp , depComb.recebeAditivo(param) );
  }

  //Teste de particionamento e valor limite do metodo que abastece o tanque de gasolina parcialmente cheio
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "9000, 9000",
      "9001, 9000"
    }
  )
  public void recebeGasolinaTanqParcialmenteCheio(int param, int resEsp){
    depComb.recebeGasolina(1000);
    assertEquals( resEsp , depComb.recebeGasolina(param) );
  }

  //Teste de particionamento e valor limite do metodo que abastece os tanques de alcool parcialmente cheios
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "2000, 2000",
      "2001, 2000"
    }
  )
  public void recebeAlcoolTanqParcialmenteCheio(int param, int resEsp){
    depComb.recebeAlcool(500);
    assertEquals( resEsp , depComb.recebeAlcool(param) );
  }

  //Testes com os tanques cheios
  @Test
  public void recebeAditivoTanqCheio(){
    depComb.recebeAditivo(500);
    assertEquals( 0 , depComb.recebeAditivo(100) );
  }

  @Test
  public void recebeGasolinaTanqCheio(){
    depComb.recebeGasolina(10000);
    assertEquals( 0 , depComb.recebeGasolina(100) );
  }

  @Test
  public void recebeAlcoolTanqCheio(){
    depComb.recebeAlcool(2500);
    assertEquals( 0 , depComb.recebeAlcool(100) );
  }

  //Teste com valores inválidos
  @Test
  public void recebeValorInvalidoAditivo(){
    assertEquals( -1 , depComb.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoGasolina(){
    assertEquals( -1 , depComb.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoAlcool(){
    assertEquals( -1 , depComb.recebeAlcool(-10) );
  }

  //TESTE DA FUNCAO DE ENCOMENDA DE COMBUSTIVEL
  //Testes de partição e valor limite das encomendas no estado NORMAL
  @ParameterizedTest
  @CsvSource( delimiter = ';', value =
    {
      "10000; [500, 7000, 1250, 1250]",
      "-10;   [-7, 0, 0, 0]",
      "13000; [-21, 0, 0, 0]"
    }
  )
  public void encomendaCombustivelNormal(int qtd, String res){
    depComb.recebeAditivo(500);
    depComb.recebeAlcool(2500);
    depComb.recebeGasolina(10000);
    depComb.defineSituacao();
    assertEquals( res , Arrays.toString(depComb.encomendaCombustivel(qtd, DepComb.TIPOPOSTO.COMUM)) );
  }

  //Testes de partição e valor limite das encomendas no estado SOBRAVISO 
  @ParameterizedTest
  @CsvSource( delimiter = ';', value =
    {
      "6000; COMUM; [150, 2100, 375, 375]",       //POSTO COMUM
      "12001; COMUM; [-21, 0, 0, 0]",
      "-1; COMUM; [-7, 0, 0, 0]",
      "6000; ESTRATEGICO; [300, 4200, 750, 750]", //POSTO ESTRATEGICO
      "6001; ESTRATEGICO; [-21, 0, 0, 0]",
      "-1; ESTRATEGICO; [-7, 0, 0, 0]"
    }
  )
  public void encomendaCombustivelSobraviso(int qtd, String posto,String res){
    depComb.recebeAditivo(300);
    depComb.recebeAlcool(1500);
    depComb.recebeGasolina(4200);
    depComb.defineSituacao();
    assertEquals( res , Arrays.toString( depComb.encomendaCombustivel(qtd, Enum.valueOf(DepComb.TIPOPOSTO.class , posto)) ) ) ;
  }

  //Teste de partição e valor limite das encomendas no estado EMERGENCIA
  @ParameterizedTest
  @CsvSource( delimiter = ';', value =
    {
      "1000; COMUM; [-14, 0, 0, 0]",
      "1000; ESTRATEGICO; [50, 700, 125, 125]",
      "2000; ESTRATEGICO; [100, 1400, 250, 250]",
      "3500; ESTRATEGICO; [-21, 0, 0, 0]",
      "-5;   ESTRATEGICO; [-7, 0, 0, 0]"
    }
  )
  public void encomendaCombustivelEmergencia(int qtd, String posto, String res){
    depComb.recebeAditivo(100);
    depComb.recebeAlcool(750);
    depComb.recebeGasolina(2100);
    depComb.defineSituacao();
    assertEquals( res , Arrays.toString( depComb.encomendaCombustivel(qtd, Enum.valueOf(DepComb.TIPOPOSTO.class , posto)) ) );
  }

  //TESTE DE ESTADOS
  //Teste baseado em modelos para os tanques em estado NORMAL
  //Estado NORMAL => NORMAL
  //       NORMAL => SOBREAVISO
  //       NORMAL => EMERGENCIA
  @ParameterizedTest
  @CsvSource(
    {
      "1000, NORMAL",
      "5500, SOBRAVISO",
      "9000, EMERGENCIA"
    }
  )
  public void situacaoNormal(int qtd, String situacao){
    depComb.recebeAditivo(500);
    depComb.recebeAlcool(2500);
    depComb.recebeGasolina(7000);     //COMBUSTIVEL = 10.000
    depComb.defineSituacao();
    depComb.encomendaCombustivel(qtd, DepComb.TIPOPOSTO.COMUM);
    depComb.defineSituacao();
    assertEquals( Enum.valueOf(DepComb.SITUACAO.class, situacao) , depComb.getSituacao() );
  }

  //Estado SOBREAVISO => SOBREAVISO
  //       SOBREAVISO => EMERGENCIA
  @ParameterizedTest
  @CsvSource(
    {
      "1000, SOBRAVISO",
      "4000, EMERGENCIA"
    }
  )
  public void situacaoSobraviso(int qtd, String situacao){
    depComb.recebeAditivo(200);
    depComb.recebeAlcool(1000);
    depComb.recebeGasolina(4000);      
    depComb.defineSituacao();
    depComb.encomendaCombustivel(qtd, DepComb.TIPOPOSTO.COMUM);
    depComb.defineSituacao();
    assertEquals( Enum.valueOf(DepComb.SITUACAO.class, situacao) , depComb.getSituacao() );
  }

  //Estado SOBREAVISO => NORMAL
  @Test
  public void situacaoSobreavisoNormal(){
    depComb.recebeAditivo(200);
    depComb.recebeAlcool(1000);
    depComb.recebeGasolina(4000);
    depComb.defineSituacao();
    depComb.recebeAditivo(100);
    depComb.recebeAlcool(1000);
    depComb.recebeGasolina(2000);
    depComb.defineSituacao();
    assertEquals( DepComb.SITUACAO.NORMAL , depComb.getSituacao() );
  }

  //Estado  EMERGENCIA => EMERGENCIA
  //        EMERGENCIA => SOBRAVISO
  //        EMERGENCIA => NORMAL
  @ParameterizedTest
  @CsvSource(
    {
      "10, 10, 10, EMERGENCIA",
      "100, 500, 2000, SOBRAVISO",
      "300, 1000, 7000, NORMAL"
    }
  )
  public void situacaoEmergencia(int aditivo, int alcool, int gasolina, String situacao){
    depComb.recebeAditivo(50);
    depComb.recebeAlcool(300);
    depComb.recebeGasolina(1000);      
    depComb.defineSituacao();
    assertEquals( DepComb.SITUACAO.EMERGENCIA , depComb.getSituacao() );
    depComb.recebeAditivo(aditivo);
    depComb.recebeAlcool(alcool);
    depComb.recebeGasolina(gasolina);      
    depComb.defineSituacao();
    assertEquals( Enum.valueOf(DepComb.SITUACAO.class, situacao) , depComb.getSituacao() );
  }
}
