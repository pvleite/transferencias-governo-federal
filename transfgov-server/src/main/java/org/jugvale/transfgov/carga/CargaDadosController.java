package org.jugvale.transfgov.carga;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jugvale.transfgov.model.base.Area;
import org.jugvale.transfgov.model.base.Estado;
import org.jugvale.transfgov.model.base.Municipio;
import org.jugvale.transfgov.model.transferencia.Acao;
import org.jugvale.transfgov.model.transferencia.CargaTransfInfo;
import org.jugvale.transfgov.model.transferencia.Favorecido;
import org.jugvale.transfgov.model.transferencia.Programa;
import org.jugvale.transfgov.model.transferencia.SubFuncao;
import org.jugvale.transfgov.model.transferencia.Transferencia;
import org.jugvale.transfgov.service.impl.AcaoService;
import org.jugvale.transfgov.service.impl.AreaService;
import org.jugvale.transfgov.service.impl.CargaTransfInfoService;
import org.jugvale.transfgov.service.impl.EstadoService;
import org.jugvale.transfgov.service.impl.FavorecidoService;
import org.jugvale.transfgov.service.impl.FonteFinalidadeService;
import org.jugvale.transfgov.service.impl.MunicipioService;
import org.jugvale.transfgov.service.impl.ProgramaService;
import org.jugvale.transfgov.service.impl.SubFuncaoService;
import org.jugvale.transfgov.service.impl.TransferenciaService;

@Stateless 
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CargaDadosController {

	@Inject
	Logger logger;

	@Inject
	CargaTransfInfoService cargaTransfInfoService;

	@Inject
	TransferenciaService transferenciaService;

	@Inject
	MunicipioService municipioService;

	@Inject
	AcaoService acaoService;

	@Inject
	EstadoService estadoService;

	@Inject
	AreaService areaService;

	@Inject
	SubFuncaoService subFuncaoService;

	@Inject
	ProgramaService programaService;

	@Inject
	FonteFinalidadeService fonteFinalidadeService;

	@Inject
	FavorecidoService favorecidoService;

	/**
	 * Irá carregar o arquivo passado no banco de dados. Linha a linha será
	 * inserida.
	 * 
	 * @param arquivoCSV
	 *            O arquivo para ser carregado
	 * @throws IOException
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void carregarNoBanco(int ano, int mes, Path arquivoCSV)
			throws IOException {
		logger.warning("Iniciando carga para data " + mes + "/" + ano);
		CargaTransfInfo cargaTransfInfo = cargaTransfInfoService
				.porAnoMesOuCria(ano, mes, () -> new CargaTransfInfo(ano, mes));
		cargaTransfInfo.setInicio(new Date());
		cargaTransfInfo.setFim(null);
		cargaTransfInfoService.atualizar(cargaTransfInfo);
		AtomicInteger totalSucesso = new AtomicInteger(0);
		AtomicInteger totalFalha = new AtomicInteger(0);
		AtomicInteger totalNaoProcessada = new AtomicInteger(0);
		AtomicInteger totalLinhas = new AtomicInteger();
		Files.lines(arquivoCSV, StandardCharsets.UTF_8).skip(1)
				.forEach(linha -> {
					totalLinhas.incrementAndGet();
					try {
						if (!salvarLinha(ano, mes, linha)) {
							totalNaoProcessada.incrementAndGet();
						} else {
							totalSucesso.incrementAndGet();
						}
					} catch (Exception e) {
						totalFalha.incrementAndGet();
						e.printStackTrace();
					}
					cargaTransfInfo.setQtdeLinhas(totalLinhas.get());
					cargaTransfInfo.setQtdeNaoProcessada(totalNaoProcessada.get());
					cargaTransfInfo.setQtdeFalhas(totalFalha.get());
					cargaTransfInfo.setQtdeSucesso(totalSucesso.get());
					cargaTransfInfoService.atualizar(cargaTransfInfo);
				});
		Files.delete(arquivoCSV);
		cargaTransfInfo.setFim(new Date());
		cargaTransfInfo.setQtdeSucesso(totalSucesso.get());
		cargaTransfInfoService.atualizar(cargaTransfInfo);
		logger.warning("Carga para " + mes + "/" + ano + " terminada");

	}

	/**
	 * 
	 * Irá pegar cada dado do CSV e salvar no banco de dados. Campos do CSV: --
	 * Sigla Unidade Federação Codigo SIAFI Municipio Nome Municipio Codigo
	 * Funcao Nome Funcao Codigo Sub Funcao Nome Sub Funcao Codigo Programa Nome
	 * Programa Codigo Acao Nome Acao Linguagem Cidadã Codigo Favorecido Nome
	 * Favorecido Fonte-Finalidade Modalidade Aplicação Número Convênio Valor
	 * Parcela --
	 * 
	 * @param linha
	 * @throws Exception
	 * @return Se a linha foi processada ou não
	 *
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean salvarLinha(int ano, int mes, String linha) throws Exception {
		String[] campos = linha.split("\\t");
		if (campos.length != 18) {
			return false;
		}
		String siglaEstado = campos[0];
		String siafiMunicipio = campos[1];
		String nomeMunicipio = campos[2];
		long codigoFuncao = Long.parseLong(campos[3]);
		String nomeFuncao = campos[4];
		long codigoSubFuncao = Long.parseLong(campos[5]);
		String nomeSubFuncao = campos[6];
		long codigoPrograma = Long.parseLong(campos[7]);
		String nomePrograma = campos[8];
		String codigoAcao = campos[9];
		String nomeAcao = campos[10];
		String nomePopular = campos[11];
		String codigoFavorecido = campos[12];
		String nomeFavorecido = campos[13];
		// GAMBIARRA PARA EVITAR PROBLEMAS COM LOCALE DE FLOATS
		float valor = Float.parseFloat(campos[17].replaceAll("\\,", "")
				.replace("\\.", ","));

		Estado estado = estadoService.buscaEstadoPorSiglaOuCria(siglaEstado,
				() -> new Estado(siglaEstado));
		Municipio municipio = municipioService.porEstadoNomeESIAFIOuCria(
				estado, nomeMunicipio, siafiMunicipio, () -> new Municipio(
						siafiMunicipio, nomeMunicipio, estado));
		Area area = areaService.buscaPorIdOuCria(codigoFuncao, () -> new Area(
				codigoFuncao, nomeFuncao));
		SubFuncao subFuncao = subFuncaoService.buscaPorIdOuCria(
				codigoSubFuncao, () -> new SubFuncao(codigoSubFuncao,
						nomeSubFuncao, area));
		Programa programa = programaService.buscaPorIdOuCria(codigoPrograma,
				() -> new Programa(codigoPrograma, nomePrograma));
		Acao acao = acaoService.buscaPorCodigoOuCria(codigoAcao,
				() -> new Acao(codigoAcao, nomeAcao, nomePopular));
		Favorecido favorecido = favorecidoService.buscaPorCodigoOuCria(
				codigoFavorecido, () -> new Favorecido(nomeFavorecido,
						codigoFavorecido));
		Transferencia transferencia = new Transferencia();
		transferencia.setAno(ano);
		transferencia.setMes(mes);
		transferencia.setAcao(acao);
		transferencia.setFavorecido(favorecido);

		transferencia.setMunicipio(municipio);
		transferencia.setPrograma(programa);
		transferencia.setSubFuncao(subFuncao);
		transferencia.setValor(valor);
		transferenciaService.salvar(transferencia);
		return true;
	}
}
