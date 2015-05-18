package org.jugvale.transparencia.transf.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import org.jugvale.transparencia.transf.model.base.Area;
import org.jugvale.transparencia.transf.model.base.Estado;
import org.jugvale.transparencia.transf.model.base.Municipio;
import org.jugvale.transparencia.transf.model.transferencia.Transferencia;
import org.jugvale.transparencia.transf.service.Service;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TransferenciaService extends Service<Transferencia> {

	/**
	 * Limpa todos os dados de transferencias para esse ano e mes
	 * 
	 * @param ano
	 * @param mes
	 */
	public void limpaTransferencias(int ano, int mes) {
		// TODO
	}

	/**
	 * 
	 * Verifica se há transferências para aquele ano e mês
	 * @param ano
	 * @param mes
	 * @return
	 */
	public boolean temTranferencia(int ano, int mes) {		
		TypedQuery<Long> buscaTransferencia = em.createNamedQuery("Transferencia.quantidadePorMesEAno", Long.class);
		buscaTransferencia.setParameter("ano", ano);
		buscaTransferencia.setParameter("mes", mes);
		return buscaTransferencia.getSingleResult() > 0;
	}
	
	public boolean temTranferencia(int ano) {		
		TypedQuery<Long> buscaTransferencia = em.createNamedQuery("Transferencia.quantidadePorAno", Long.class);
		buscaTransferencia.setParameter("ano", ano);
		return buscaTransferencia.getSingleResult() > 0;
	}	

	public List<Transferencia> buscarPorAnoMesMunicipio(int ano, int mes, Municipio municipio) {
		TypedQuery<Transferencia> buscaTransferencia = em.createNamedQuery("Transferencia.porAnoMesMunicipio", Transferencia.class);
		buscaTransferencia.setParameter("ano", ano);
		buscaTransferencia.setParameter("mes", mes);
		buscaTransferencia.setParameter("municipio", municipio);
		return buscaTransferencia.getResultList();
	}
	
	public List<Transferencia> buscarPorAnoMunicipio(int ano, Municipio municipio) {
		TypedQuery<Transferencia> buscaTransferencia = em.createNamedQuery("Transferencia.porAnoMunicipio", Transferencia.class);
		buscaTransferencia.setParameter("ano", ano);
		buscaTransferencia.setParameter("municipio", municipio);
		return buscaTransferencia.getResultList();
	}

	public List<Transferencia> buscarPorAnoMesEstado(int ano, int mes, Estado estado) {
		TypedQuery<Transferencia> buscaTransferencia = em.createNamedQuery("Transferencia.porAnoMesEstado", Transferencia.class);
		buscaTransferencia.setParameter("ano", ano);
		buscaTransferencia.setParameter("mes", mes);
		buscaTransferencia.setParameter("estado", estado);
		return buscaTransferencia.getResultList();
	}

	public List<Transferencia> buscarPorAnoMesAreaMunicipio(int ano, int mes, Area area, Municipio municipio) {
		TypedQuery<Transferencia> buscaTransferencia = em.createNamedQuery("Transferencia.porAnoMesAreaMunicipio", Transferencia.class);
		buscaTransferencia.setParameter("ano", ano);
		buscaTransferencia.setParameter("mes", mes);
		buscaTransferencia.setParameter("area", area);
		buscaTransferencia.setParameter("municipio", municipio);
		return buscaTransferencia.getResultList();
	}	

}
