package pucpr.br.pggia.pibic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import pucpr.br.ppgia.pibic.model.Commit;
import pucpr.br.ppgia.pibic.model.Project;

import com.mysql.jdbc.PreparedStatement;

public class pibicDAO {
	
	public Connection getConnection() throws SQLException {

		String url = "jdbc:mysql://localhost:3306/"; 
		String dbName = "pibic";
		String driver = "com.mysql.jdbc.Driver"; 
		String userName = "root"; 
		String password = "root";
		Connection conn = null;
		try { 
				Class.forName(driver).newInstance(); 
				conn = DriverManager.getConnection(url+dbName,userName,password); 
				return conn;
//				conn.close(); 
			}
		catch (Exception e) {
			e.printStackTrace(); }
		
	    return null;
	}
	
	public void inserirCommit(List<Commit> commits)
	{
		Connection c;
		int ce = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			c = getConnection();
			PreparedStatement project;
			ResultSet rs;

			for (int i = 0; i < commits.size(); i++) {
				project = (PreparedStatement) c.prepareStatement("SELECT id from PROJECT where project=?");
				project.setString(1, commits.get(i).getProject().getName());
				
				rs = project.executeQuery();
				
				if(!rs.first())
				{
					project = (PreparedStatement) c.prepareStatement("INSERT INTO pibic.project (project) values (?)", PreparedStatement.RETURN_GENERATED_KEYS);
					project.setString(1, commits.get(i).getProject().getName());
					project.execute();
					rs = project.getGeneratedKeys();
					
					 while (rs.next()) {
						 ce = rs.getInt(1);
					 }
				}	
				else
				{
					ce = rs.getInt(1);
					
					commits.get(i).getProject().setId(ce);
					    
						//ce = rs.getInt("id");			    
					    // etc.
				
				}

				String timeNow = sf.format(commits.get(i).getDate().getTime());
				
				
				project =  (PreparedStatement) c.prepareStatement("INSERT INTO commit (id_commit, author, date, message, fk_project) values (?,?,?,?,?)");
				
				project.setString(1, commits.get(i).getSha());
				project.setString(2, commits.get(i).getNome());
				project.setString(3, timeNow);
				project.setString(4, commits.get(i).getMessage());
				project.setInt(5, ce);
				
				project.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void inserirProjetos(List<Project> projects) {
		Connection c;

		try {
			c = getConnection();
			PreparedStatement project;
			ResultSet rs;

			for (int i = 0; i < projects.size(); i++) {
				project = (PreparedStatement) c.prepareStatement("SELECT id from PROJECT where project=?");
				project.setString(1, projects.get(i).getName());
				
				rs = project.executeQuery();
				
				if(!rs.first())
				{
					project = (PreparedStatement) c.prepareStatement("INSERT INTO pibic.project (project, language) values (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
					project.setString(1, projects.get(i).getName());
					project.setString(2, projects.get(i).getLanguage());
					project.execute();
					rs = project.getGeneratedKeys();
					
					while (rs.next()) {
						projects.get(i).setId( rs.getInt(1));
						
					}
				}
				else
				{
					project = (PreparedStatement) c.prepareStatement("UPDATE pibic.project set language= ? WHERE project = ?");
					project.setString(1, projects.get(i).getLanguage());
					project.setString(2, projects.get(i).getName());
					project.execute();
					
					project = (PreparedStatement) c.prepareStatement("SELECT LAST_INSERT_ID()");
					
					rs = project.executeQuery();
					
					while (rs.next()) {
						projects.get(i).setId( rs.getInt(1));
						
					}
					
					this.inserirUsers(projects.get(i));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}	
	
	public void inserirUsers(Project p) {
		Connection c;

		try {
			c = getConnection();
			PreparedStatement project;
			ResultSet rs;
			
				project = (PreparedStatement) c.prepareStatement("SELECT id_user from pibic.users where user_name=?");
				project.setString(1, p.getUser().getUser());

				
				rs = project.executeQuery();
				
				if(!rs.first())
				{
					project = (PreparedStatement) c.prepareStatement("INSERT INTO pibic.users (user_name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS);
					project.setString(1, p.getUser().getUser());
			
					project.execute();
					rs = project.getGeneratedKeys();
					
					p.getUser().setId(rs.getInt(1));
					
				}
				else
				{						
						p.getUser().setId(rs.findColumn("id_user"));
				}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}	
	

}
