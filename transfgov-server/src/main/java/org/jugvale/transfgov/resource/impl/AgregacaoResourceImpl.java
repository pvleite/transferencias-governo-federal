package org.jugvale.transfgov.resource.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jugvale.transfgov.agregacao.AgregacaoController;
import org.jugvale.transfgov.model.agregacao.Agregacao;
import org.jugvale.transfgov.model.agregacao.TipoAgregacao;
import org.jugvale.transfgov.model.base.Area;
import org.jugvale.transfgov.model.base.Estado;
import org.jugvale.transfgov.model.base.Municipio;
import org.jugvale.transfgov.model.transferencia.Transferencia;
import org.jugvale.transfgov.resource.AgregacaoResource;
import org.jugvale.transfgov.service.impl.AreaService;
import org.jugvale.transfgov.service.impl.EstadoService;
import org.jugvale.transfgov.service.impl.MunicipioService;
import org.jugvale.transfgov.service.impl.TransferenciaService;
import org.jugvale.transfgov.utils.JaxrsUtils;

@Stateless
public class AgregacaoResourceImpl implements AgregacaoResource {
	
	final String MSG_AGREGACAO_ERRADA = "A agregação %s não é permitida em agregações %s";
	final String MSG_NAO_HA_DADOS_ANO_MES =  "Não há dados de transferência para ano %d e mês %d";
	final String MSG_NAO_HA_DADOS_ANO  =  "Não há dados de transferência para ano %d";

	
	@Inject
	AgregacaoController agregacaoController;
	
	@Inject
	MunicipioService municipioService;
	
	@Inject
	TransferenciaService transferenciaService;

	@Inject
	EstadoService estadoService;

	@Inject
	private AreaService areaService;
	
	public TipoAgregacao[] todasAgregacoes() {
		return TipoAgregacao.values();
	}
	
	public Agregacao agregaPorAnoMesMunicipio(TipoAgregacao tipoAgregacao, int ano, int mes, long idMunicipio) {
		JaxrsUtils.lanca404SeFalso(transferenciaService.temTranferencia(ano, mes), String.format(MSG_NAO_HA_DADOS_ANO_MES, ano, mes));
		Municipio municipio = JaxrsUtils.lanca404SeNulo(municipioService.buscarPorId(idMunicipio), Municipio.class);
		List<Transferencia> transferencias = transferenciaService.buscarPorAnoMesMunicipio(ano, mes, municipio);
		return agregacaoController.agregaPorTipo(ano, mes, municipio.getEstado(), municipio, tipoAgregacao, transferencias);
	}
	
	public Agregacao agregaPorAnoMunicipio(TipoAgregacao tipoAgregacao, int ano, long idMunicipio) {
		JaxrsUtils.lanca404SeFalso(transferenciaService.temTranferencia(ano), String.format(MSG_NAO_HA_DADOS_ANO, ano));
		Municipio municipio = JaxrsUtils.lanca404SeNulo(municipioService.buscarPorId(idMunicipio), Municipio.class);
		List<Transferencia> transferencias = transferenciaService.buscarPorAnoMunicipio(ano, municipio);
		return agregacaoController.agregaPorTipo(ano, 0, municipio.getEstado(), municipio, tipoAgregacao, transferencias);
	}
	

	public Agregacao agregaPorAnoMesAreaMunicipio(long idArea, int ano, int mes, long idMunicipio) {
		JaxrsUtils.lanca404SeFalso(transferenciaService.temTranferencia(ano, mes), String.format(MSG_NAO_HA_DADOS_ANO_MES, ano, mes));
		Area area = JaxrsUtils.lanca404SeNulo(areaService.buscarPorId(idArea), Area.class);
		Municipio municipio = JaxrsUtils.lanca404SeNulo(municipioService.buscarPorId(idMunicipio), Municipio.class);
		List<Transferencia> transferencias = transferenciaService.buscarPorAnoMesAreaMunicipio(ano, mes, area, municipio);
		return agregacaoController.agregaPorTipo(ano, 0, municipio.getEstado(), municipio, TipoAgregacao.SUB_FUNCAO, transferencias);
	}
	
	public Agregacao agregaPorAnoEstado(int ano,  int mes, String siglaEstado) {
		Estado estado = JaxrsUtils.lanca404SeNulo(estadoService.buscaEstadoPorSigla(siglaEstado), Estado.class);
		List<Transferencia> transferencias = transferenciaService.buscarPorAnoMesEstado(ano, mes, estado);
		return agregacaoController.agregaPorTipo(ano, 0, estado, null, TipoAgregacao.MUNICIPIO, transferencias);
	}

}