package org.jugvale.transfgov.model.transferencia;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jugvale.transfgov.model.base.Area;

@Entity
@Table(name = "sub_funcao")
@XmlRootElement
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="cache-classes-basicas")
public class SubFuncao {

	@Id
	@Column(name = "sfu_id")
	private long id;
	
	@Column(name = "sfu_nome")
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "area_area_id")
	private Area area;
	
	public SubFuncao() {
		super();
	}

	public SubFuncao(long codigoSubFuncao, String nomeSubFuncao, Area area) {
		this.id = codigoSubFuncao;
		this.nome = nomeSubFuncao;
		this.area = area;
	}
	
	@Override
	public String toString() {
		return area.getNome() + ": " + nome;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

}
