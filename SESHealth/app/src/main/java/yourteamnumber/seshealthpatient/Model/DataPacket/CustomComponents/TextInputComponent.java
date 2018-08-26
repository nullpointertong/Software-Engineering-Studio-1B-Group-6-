package yourteamnumber.seshealthpatient.Model.DataPacket.CustomComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.TextData;
import yourteamnumber.seshealthpatient.R;

public class TextInputComponent extends LinearLayout {

    private MaterialEditText etDescription;

    public TextInputComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_input_component, this, true);

        etDescription = findViewById(R.id.etDescription);
    }

    public TextInputComponent(Context context) {
        this(context, null);
    }

    public TextData getTextData()
    {
        return new TextData(etDescription.getText().toString());
    }
}