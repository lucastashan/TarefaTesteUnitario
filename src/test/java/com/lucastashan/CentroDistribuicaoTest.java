package com.lucastashan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CentroDistribuicaoTest {
  private CentroDistribuicao centroDistribuicao = null;


  @Test
  public void testeContrutor(){
    CentroDistribuicao centroDistribuicao = new CentroDistribuicao(10, 10000, 1250, 1250);
    assertEquals( CentroDistribuicao.SITUACAO.EMERGENCIA , centroDistribuicao.getSituacao() );
  }

  @Test
  public void testeContrutorIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new CentroDistribuicao(10, 10000, 0, 1250);
    });
      
    String expectedMessage = "ILLEGAL_ARGUMENT_EXCEPTION";
    String actualMessage = exception.getMessage();

    Assertions.assertEquals(expectedMessage, actualMessage);
  }
  
  @BeforeEach
  void setUp(){
    centroDistribuicao = new CentroDistribuicao(0,0,0,0);
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
    assertEquals( resEsp , centroDistribuicao.recebeAditivo(param) );
  }

  // Teste de particionamento e valor limite do metodo que abastece o tanque de gasolina vazio
  @ParameterizedTest
  @CsvSource(
    {
      "100, 100",
      "10000, 10000",
      "10001, 10000"
    }
  )
  public void recebeGasolinaTanqVazio(int param, int resEsp){
    assertEquals( resEsp , centroDistribuicao.recebeGasolina(param) );
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
    assertEquals( resEsp , centroDistribuicao.recebeAlcool(param) );
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
    centroDistribuicao.recebeAditivo(100);
    assertEquals( resEsp , centroDistribuicao.recebeAditivo(param) );
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
    centroDistribuicao.recebeGasolina(1000);
    assertEquals( resEsp , centroDistribuicao.recebeGasolina(param) );
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
    centroDistribuicao.recebeAlcool(500);
    assertEquals( resEsp , centroDistribuicao.recebeAlcool(param) );
  }

  //Testes com os tanques cheios
  @Test
  public void recebeAditivoTanqCheio(){
    centroDistribuicao.recebeAditivo(500);
    assertEquals( 0 , centroDistribuicao.recebeAditivo(100) );
  }

  @Test
  public void recebeGasolinaTanqCheio(){
    centroDistribuicao.recebeGasolina(10000);
    assertEquals( 0 , centroDistribuicao.recebeGasolina(100) );
  }

  @Test
  public void recebeAlcoolTanqCheio(){
    centroDistribuicao.recebeAlcool(2500);
    assertEquals( 0 , centroDistribuicao.recebeAlcool(100) );
  }

  //Teste com valores inv??lidos
  @Test
  public void recebeValorInvalidoAditivo(){
    assertEquals( -1 , centroDistribuicao.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoGasolina(){
    assertEquals( -1 , centroDistribuicao.recebeAditivo(-10) );
  }

  @Test
  public void recebeValorInvalidoAlcool(){
    assertEquals( -1 , centroDistribuicao.recebeAlcool(-10) );
  }

  //TESTE DA FUNCAO DE ENCOMENDA DE COMBUSTIVEL
  //Testes de parti????o e valor limite das encomendas no estado NORMAL
  @ParameterizedTest
  @CsvSource( delimiter = ';', value =
    {
      "10000; [500, 7000, 1250, 1250]",
      "-10;   [-7, 0, 0, 0]",
      "13000; [-21, 0, 0, 0]"
    }
  )
  public void encomendaCombustivelNormal(int qtd, String res){
    centroDistribuicao.recebeAditivo(500);
    centroDistribuicao.recebeAlcool(2500);
    centroDistribuicao.recebeGasolina(10000);
    centroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString(centroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM)) );
  }

  //Testes de parti????o e valor limite das encomendas no estado SOBRAVISO 
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
    centroDistribuicao.recebeAditivo(300);
    centroDistribuicao.recebeAlcool(1500);
    centroDistribuicao.recebeGasolina(4200);
    centroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString( centroDistribuicao.encomendaCombustivel(qtd, Enum.valueOf(CentroDistribuicao.TIPOPOSTO.class , posto)) ) ) ;
  }

  //Teste de parti????o e valor limite das encomendas no estado EMERGENCIA
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
    centroDistribuicao.recebeAditivo(100);
    centroDistribuicao.recebeAlcool(750);
    centroDistribuicao.recebeGasolina(2100);
    centroDistribuicao.defineSituacao();
    assertEquals( res , Arrays.toString( centroDistribuicao.encomendaCombustivel(qtd, Enum.valueOf(CentroDistribuicao.TIPOPOSTO.class , posto)) ) );
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
    centroDistribuicao.recebeAditivo(500);
    centroDistribuicao.recebeAlcool(2500);
    centroDistribuicao.recebeGasolina(7000);     //COMBUSTIVEL = 10.000
    centroDistribuicao.defineSituacao();
    centroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM);
    centroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , centroDistribuicao.getSituacao() );
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
    centroDistribuicao.recebeAditivo(200);
    centroDistribuicao.recebeAlcool(1000);
    centroDistribuicao.recebeGasolina(4000);      
    centroDistribuicao.defineSituacao();
    centroDistribuicao.encomendaCombustivel(qtd, CentroDistribuicao.TIPOPOSTO.COMUM);
    centroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , centroDistribuicao.getSituacao() );
  }

  //Estado SOBREAVISO => NORMAL
  @Test
  public void situacaoSobreavisoNormal(){
    centroDistribuicao.recebeAditivo(200);
    centroDistribuicao.recebeAlcool(1000);
    centroDistribuicao.recebeGasolina(4000);
    centroDistribuicao.defineSituacao();
    centroDistribuicao.recebeAditivo(100);
    centroDistribuicao.recebeAlcool(1000);
    centroDistribuicao.recebeGasolina(2000);
    centroDistribuicao.defineSituacao();
    assertEquals( CentroDistribuicao.SITUACAO.NORMAL , centroDistribuicao.getSituacao() );
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
    centroDistribuicao.recebeAditivo(50);
    centroDistribuicao.recebeAlcool(300);
    centroDistribuicao.recebeGasolina(1000);      
    centroDistribuicao.defineSituacao();
    assertEquals( CentroDistribuicao.SITUACAO.EMERGENCIA , centroDistribuicao.getSituacao() );
    centroDistribuicao.recebeAditivo(aditivo);
    centroDistribuicao.recebeAlcool(alcool);
    centroDistribuicao.recebeGasolina(gasolina);      
    centroDistribuicao.defineSituacao();
    assertEquals( Enum.valueOf(CentroDistribuicao.SITUACAO.class, situacao) , centroDistribuicao.getSituacao() );
  }
}
