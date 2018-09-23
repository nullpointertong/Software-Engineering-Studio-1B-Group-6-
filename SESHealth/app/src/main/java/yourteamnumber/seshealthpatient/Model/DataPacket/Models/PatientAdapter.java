package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yourteamnumber.seshealthpatient.R;

public class PatientAdapter  extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private Context mContext;
    private List<Patient> mPatientList;
    CustomItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView patientNum, patientId, firstName, lastName, gender, height, weight, medicalCondition;

        public ViewHolder(View view) {
            super(view);
            patientNum = view.findViewById(R.id.patient_num);
            patientId = view.findViewById(R.id.patient_id);
            firstName = view.findViewById(R.id.patient_firstName_tv);
            lastName = view.findViewById(R.id.patient_lastName_tv);
            gender = view.findViewById(R.id.patient_gender_tv);
            height = view.findViewById(R.id.patient_height_tv);
            weight = view.findViewById(R.id.patient_weight_tv);
            medicalCondition = view.findViewById(R.id.patient_medical_condition_tv);
        }

    }

    public PatientAdapter(Context context, List<Patient> patientList) {
        this.mContext = context;
        this.mPatientList = patientList;
    }

    public PatientAdapter(Context context, List<Patient> patientList, CustomItemClickListener listener) {
        this.mContext = context;
        this.mPatientList = patientList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Patient patient = mPatientList.get(position);

        holder.patientNum.setText(""+position+1);
        holder.patientId.setText(patient.getPatientId());
        holder.firstName.setText(String.format(holder.firstName.getText().toString(), patient.getFirstName().toString()));
        holder.lastName.setText(String.format(holder.lastName.getText().toString(), patient.getLastName().toString()));
        holder.gender.setText(String.format(holder.gender.getText().toString(), patient.getGender().toString()));
        holder.height.setText(""+patient.getHeight());
        holder.weight.setText(""+patient.getWeight());

        if (patient.getMedicalCondition() == null) {
            holder.medicalCondition.setText("");
        } else {
            String temp = patient.getMedicalCondition();
            if (temp.length() < 20) {
                holder.medicalCondition.setText(patient.getMedicalCondition());
            } else {
                holder.medicalCondition.setText(patient.getMedicalCondition().substring(0, 18) + "...");
            }
        }

    }

    @Override
    public int getItemCount() {
        return mPatientList.size();
    }
}


