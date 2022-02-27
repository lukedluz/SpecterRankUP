package com.lucas.specterrankup.objetos;

public class Rank {

	private Double coins, runas, blocos;
	private String nome, prefix;
	private Integer posicao;
	private boolean defaultb;

	public Rank(String nome, String prefix, Double coins, Double runas, Double blocos, Integer posicao, boolean defaultb) {
		this.nome = nome;
		this.coins = coins;
		this.runas = runas;
		this.blocos = blocos;
		this.posicao = posicao;
		this.prefix = prefix;
		this.defaultb = defaultb;
	}

	public String toString() {
		return nome;
	}

	public Double getCoins() {
		return coins;
	}

	public boolean getDefault() {
		return defaultb;
	}

	public Double getRunas() {
		return runas;
	}

	public Double getBlocos() {
		return blocos;
	}

	public String getNome() {
		return nome;
	}

	public String getPrefix() {
		return prefix;
	}

	public Integer getPosicao() {
		return posicao;
	}
}
