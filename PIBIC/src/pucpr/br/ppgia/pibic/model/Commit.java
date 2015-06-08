package pucpr.br.ppgia.pibic.model;

import java.util.Date;

public class Commit {
	
	private Date date;
	private String nome;
	private String message;
	private int qntCommits;
	private Project project;
	private String sha;
	
	public Commit()
	{
		qntCommits = 1;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void addCommit() {
		// TODO Auto-generated method stub
		qntCommits +=  1;
	}
	
	public void setQntCommits(int set) {
		qntCommits = set;
	}
	
	public int getQntCommits() {
		return qntCommits;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	
	
	
	
	
}
