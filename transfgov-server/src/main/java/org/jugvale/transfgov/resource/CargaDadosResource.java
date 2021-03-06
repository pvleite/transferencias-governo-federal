package org.jugvale.transfgov.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jugvale.transfgov.model.transferencia.CargaTransfInfo;

/**
 * 
 * Interface REST para permitir a carga de dados
 * 
 * @author wsiqueir
 *
 */
@Path("carga")
public interface CargaDadosResource {
	
	final String MSG_SUCESSO_AO_BAIXAR = "DADOS PARA %d/%d BAIXADOS COM SUCESSO";
	final String MSG_BAIXANDO_INICIO = "INICIANDO DOWNLOAD PARA DATA %d/%d ";
	final String MSG_SUCESSO = "Dados para ano %d e mês %d agendados para carga. Acesse GET /carga/transferencia/%d/%d/ para ver o andamento.";
	final String MSG_CARGA_REPETIDA = "Já existem dados para ano %d  mes %d. Limpe os dados antes de tentar uma nova carga";
	final String MSG_ERRO_BAIXAR = "Erro ao baixar dados. Verifique se o ano e mês estão disponíveis no portal da transparência. Mais detalhes no log do servidor";

	@POST
	@Path("transferencia/{ano}/{mes}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response baixaECarrega(@PathParam("ano") int ano, @PathParam("mes") int mes) throws IOException;

	/**
	 * Código macarrão para ler o zip de transferencia do site www.portaldatransparencia.gov.br
	 * 
	 * @param multipart
	 * @return
	 * @throws IOException
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response recebeCarga(MultipartInput conteudo) throws IOException;

	@GET
	@Path("transferencia/{ano}/{mes}")
	@Produces(MediaType.APPLICATION_JSON)
	public CargaTransfInfo recuperaInfoCarga(@PathParam("ano") int ano, @PathParam("mes") int mes);

	@GET
	@Path("transferencia")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CargaTransfInfo> todasInfoCargas();
	
}
