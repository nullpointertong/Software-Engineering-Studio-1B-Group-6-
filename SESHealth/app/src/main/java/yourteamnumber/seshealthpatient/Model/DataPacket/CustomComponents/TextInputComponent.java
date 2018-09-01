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

    public TextInputComponent(Context context) {
        super(context);
            init();
    }

    public TextInputComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
            init();
    }

    public TextInputComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
            init();
    }

    private void init()
    {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_input_component, this, true);

        etDescription = findViewById(R.id.etDescription);
    }


    public TextData getTextData()
    {
        return new TextData(etDescription.getText().toString());
    }

    public void setText(String text)
    {
        etDescription.setText(text);
    }

    public boolean isNotEmpty()
    {
        return !etDescription.getText().toString().isEmpty();
    }
}