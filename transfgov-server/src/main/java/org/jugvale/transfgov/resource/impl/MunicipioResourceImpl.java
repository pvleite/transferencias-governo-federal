package org.jugvale.transfgov.resource.impl;

import java.util.List;

import javax.inject.Inject;

import org.jugvale.transfgov.model.base.Municipio;
import org.jugvale.transfgov.resource.MunicipioResource;
import org.jugvale.transfgov.service.impl.MunicipioService;

public class MunicipioResourceImpl implements MunicipioResource {

	@Inject
	MunicipioService municipioService;

	public List<Municipio> todosMunicipios() {
		return municipioService.todos();
	}
	
	public Municipio porNomeEEstado(String sigla, String nome) {
		return municipioService.buscaPorNomeEEstado(sigla, nome);		
	}

}
