package com.lucastashan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CentroDistribuicaoTest {
  private CentroDistribuicao CentroDistribuicao = null;


  @Test
  public void testeContrutor(){
    CentroDistribuicao = new CentroDistribuicao(10, 10000, 1250, 1250);
    assertEquals( CentroDistribuicao.SITUACAO.EMERGENCIA , CentroDistribuicao.getSituacao() );
  }

  @Test
  public void testeContrutorIllegalArgument(){
    Exception exception = new CentroDistribuicao(10, 10000, 0, 1250);
    
    String expectedMessage = "ILLEGAL_ARGUMENT_EXCEPTION";
    String actualMessage = exception.getMessage();

    Assertions.assertEquals(expectedMessage, actualMessage);
  }
  
  @BeforeEach
  void setUp(){
    CentroDistribuicao = new CentroDistribuicao(0,0,0,0);
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
    assertEquals( resEsp , CentroDistribuicao.recebeAditivo(param) );
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
    assertEquals( resEsp , CentroDistribuicao.recebeGasolina(param) );
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
    assertEquals( resEsp , CentroDistribuicao.recebeAlcool(param) );
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
    CentroDistribuicao.recebeAditivo(100);
    assertEquals( resEsp , CentroDistribuicao.recebeAditivo(param) );
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
    CentroDistribuicao.recebeGasolina(1000);
    assertEquals( resEsp , CentroDistribuicao.recebeGasolina(param) );
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
    CentroDistribuicao.recebeAlcool(500);
    assertEquals( resEsp , CentroDistribuicao.recebeAlcool(param) );
  }

  //Testes com os tanques cheios
  @Test
  public void recebeAditivoTanqCheio(){
    CentroDistribuicao.recebeAditivo(500);
    assertEquals( 0 , CentroDistribuicao.recebeAditivo(100) );
  }

  @Test
  public void recebeGasolinaTanqCheio(){
    CentroDistribuicao.recebeGasolina(10000);
    assertEquals( 0 , CentroDistribuicao.recebeGasolina(100) );
  }

  @Test
  public void recebeAlcoolTanqCheio(){
    CentroDistribuicao.recebeAlcool(2500);
    assertEquals( 0 , CentroDistribuicao.recebeAlcool(100) );
  }

  //Teste com valores inválidos
  @Test
  public void recebeValorInvalidoAditivo(){
    assertEquals( -1 , CentroDistribuicao.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoGasolina(){
    assertEquals( -1 , CentroDistribuicao.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoAlcool(){
    assertEquals( -1 , CentroDistribuicao.recebeAlcool(-10) );
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
    CentroDistribuicao.recebeAditivo(500);
    CentroDistribuicao.recebeAlcool(2500);
    CentroDistribuicao.recebeGasolina(10000);
    CentroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString(CentroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM)) );
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
    CentroDistribuicao.recebeAditivo(300);
    CentroDistribuicao.recebeAlcool(1500);
    CentroDistribuicao.recebeGasolina(4200);
    CentroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString( CentroDistribuicao.encomendaCombustivel(qtd, Enum.valueOf(CentroDistribuicao.TIPOPOSTO.class , posto)) ) ) ;
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
    CentroDistribuicao.recebeAditivo(100);
    CentroDistribuicao.recebeAlcool(750);
    CentroDistribuicao.recebeGasolina(2100);
    CentroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString( CentroDistribuicao.encomendaCombustivel(qtd, Enum.valueOf(CentroDistribuicao.TIPOPOSTO.class , posto)) ) );
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
    CentroDistribuicao.recebeAditivo(500);
    CentroDistribuicao.recebeAlcool(2500);
    CentroDistribuicao.recebeGasolina(7000);     //COMBUSTIVEL = 10.000
    CentroDistribuicao.defineSituacao();
    CentroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM);
    CentroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , CentroDistribuicao.getSituacao() );
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
    CentroDistribuicao.recebeAditivo(200);
    CentroDistribuicao.recebeAlcool(1000);
    CentroDistribuicao.recebeGasolina(4000);      
    CentroDistribuicao.defineSituacao();
    CentroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM);
    CentroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , CentroDistribuicao.getSituacao() );
  }

  //Estado SOBREAVISO => NORMAL
  @Test
  public void situacaoSobreavisoNormal(){
    CentroDistribuicao.recebeAditivo(200);
    CentroDistribuicao.recebeAlcool(1000);
    CentroDistribuicao.recebeGasolina(4000);
    CentroDistribuicao.defineSituacao();
    CentroDistribuicao.recebeAditivo(100);
    CentroDistribuicao.recebeAlcool(1000);
    CentroDistribuicao.recebeGasolina(2000);
    CentroDistribuicao.defineSituacao();
    assertEquals( CentroDistribuicao.SITUACAO.NORMAL , CentroDistribuicao.getSituacao() );
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
    CentroDistribuicao.recebeAditivo(50);
    CentroDistribuicao.recebeAlcool(300);
    CentroDistribuicao.recebeGasolina(1000);      
    CentroDistribuicao.defineSituacao();
    assertEquals( CentroDistribuicao.SITUACAO.EMERGENCIA , CentroDistribuicao.getSituacao() );
    CentroDistribuicao.recebeAditivo(aditivo);
    CentroDistribuicao.recebeAlcool(alcool);
    CentroDistribuicao.recebeGasolina(gasolina);      
    CentroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , CentroDistribuicao.getSituacao() );
  }
}
