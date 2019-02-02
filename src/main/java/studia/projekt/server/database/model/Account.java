package studia.projekt.server.database.model;

public class Account {

	private Integer id;
	private String login;
	private String password;
	private String name;
	private String surname;
	private Byte sex;
	private String code;

	public Account(Integer id, String login, String password, String name, String surname, Byte sex, String code) {
		super();
		this.id = id;
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.sex = sex;
		this.code = code;
	}
	
	public Account(String login, String password, String name, String surname, Byte sex, String code) {
		super();
		this.id = null;
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.sex = sex;
		this.code = code;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Byte getSex() {
		return sex;
	}

	public void setSex(Byte sex) {
		this.sex = sex;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
