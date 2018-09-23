package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

public class Patient {
    private String mPatientId;
    private String mFirstName;
    private String mLastName;
    private String mMedicalCondition;
    private String mGender;
    private double mHeight;
    private double mWeight;

    public Patient(String patientId, String firstName, String lastName, String medicalCondition, String gender, double height, double weight) {
        mPatientId = patientId;
        mFirstName = firstName;
        mLastName = lastName;
        mMedicalCondition = medicalCondition;
        mGender = gender;
        mHeight = height;
        mWeight = weight;
    }

    public String getPatientId() {
        return mPatientId;
    }

    public void setPatientId(String patientId) {
        mPatientId = patientId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getMedicalCondition() {
        return mMedicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        mMedicalCondition = medicalCondition;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }

    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }
}
