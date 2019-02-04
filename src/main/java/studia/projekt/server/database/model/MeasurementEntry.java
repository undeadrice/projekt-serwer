package studia.projekt.server.database.model;

/**
 * klasa reprezentująca wpis wyników w bazie danych
 *
 */
public class MeasurementEntry {

	private Integer id;
	private Integer accountId;
	private Long date;
	private Double leukocyte = 0.;
	private Double erythrocyte = 0.;
	private Double hemoglobin = 0.;
	private Double hematocrit = 0.;
	private Double mcv = 0.;
	private Double mch = 0.;
	private Double mchc = 0.;
	private Double platelets = 0.;
	private Double lymphocyte = 0.;

	public MeasurementEntry(Integer id, Integer accountId, Long date, Double leukocyte, Double erythrocyte,
			Double hemoglobin, Double hematocrit, Double mcv, Double mch, Double mchc, Double platelets,
			Double lymphocyte) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.date = date;
		this.leukocyte = leukocyte;
		this.erythrocyte = erythrocyte;
		this.hemoglobin = hemoglobin;
		this.hematocrit = hematocrit;
		this.mcv = mcv;
		this.mch = mch;
		this.mchc = mchc;
		this.platelets = platelets;
		this.lymphocyte = lymphocyte;
	}

	public MeasurementEntry(Long date, Integer accountId, Double leukocyte, Double erythrocyte, Double hemoglobin,
			Double hematocrit, Double mcv, Double mch, Double mchc, Double platelets, Double lymphocyte) {
		super();
		this.id = null;
		this.accountId = accountId;
		this.date = date;
		this.leukocyte = leukocyte;
		this.erythrocyte = erythrocyte;
		this.hemoglobin = hemoglobin;
		this.hematocrit = hematocrit;
		this.mcv = mcv;
		this.mch = mch;
		this.mchc = mchc;
		this.platelets = platelets;
		this.lymphocyte = lymphocyte;
	}

	public MeasurementEntry(Integer accountId, Long date) {
		super();
		this.accountId = accountId;
		this.date = date;
	}

	public MeasurementEntry(Long date) {
		super();
		this.date = date;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getLeukocyte() {
		return leukocyte;
	}

	public void setLeukocyte(Double leukocyte) {
		this.leukocyte = leukocyte;
	}

	public Double getErythrocyte() {
		return erythrocyte;
	}

	public void setErythrocyte(Double erythrocyte) {
		this.erythrocyte = erythrocyte;
	}

	public Double getHemoglobin() {
		return hemoglobin;
	}

	public void setHemoglobin(Double hemoglobin) {
		this.hemoglobin = hemoglobin;
	}

	public Double getHematocrit() {
		return hematocrit;
	}

	public void setHematocrit(Double hematocrit) {
		this.hematocrit = hematocrit;
	}

	public Double getMcv() {
		return mcv;
	}

	public void setMcv(Double mcv) {
		this.mcv = mcv;
	}

	public Double getMch() {
		return mch;
	}

	public void setMch(Double mch) {
		this.mch = mch;
	}

	public Double getMchc() {
		return mchc;
	}

	public void setMchc(Double mchc) {
		this.mchc = mchc;
	}

	public Double getPlatelets() {
		return platelets;
	}

	public void setPlatelets(Double platelets) {
		this.platelets = platelets;
	}

	public Double getLymphocyte() {
		return lymphocyte;
	}

	public void setLymphocyte(Double lymphocyte) {
		this.lymphocyte = lymphocyte;
	}

}
