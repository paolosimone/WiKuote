package com.paolosimone.wikuote.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paolosimone.wikuote.R;
import com.paolosimone.wikuote.activity.MainActivity;
import com.paolosimone.wikuote.activity.WiKuoteNavUtils;
import com.paolosimone.wikuote.model.Category;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.WiKuoteDatabaseHelper;

import java.util.Collections;
import java.util.List;

/**
 * Dialog fragment that allow the user to choose the category to which assign a page.
 * The user can select an existing category, or insert a new one.
 */
public class SelectCategoryDialogFragment extends DialogFragment {

    public static final String TAG = "SelectCategoryDialogFragment";
    private static final String PAGE = "page";

    private Page page;

    private RadioGroup radioGroup;
    private EditText newCategoryText;

    /**
     * Build a new dialog fragment, with the goal of assigning a category to the given page.
     * @param page the page that has to be assigned
     * @return the dialog fragment instance
     */
    public static SelectCategoryDialogFragment newInstance(Page page){
        SelectCategoryDialogFragment frag = new SelectCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PAGE, page);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        page = getArguments().getParcelable(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        return inflater.inflate(R.layout.fragment_select_category, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        List<Category> categories = retrieveCategories();
        setupRadioGroup(view,categories);
        setupNewCategory(view);
        setupDialogFields(view);
    }

    private List<Category> retrieveCategories(){
        WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
        List<Category> categories = db.getAllCategories();
        return categories;
    }

    private void setupRadioGroup(View view, List<Category> categories){
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_add_categories);

        Collections.sort(categories);
        for (Category c : categories){
            RadioButton radio = new RadioButton(getActivity());
            radio.setText(c.getTitle());
            radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        newCategoryText.clearFocus();
                    }
                }
            });
            radio.setTag(c);
            radioGroup.addView(radio);
        }
    }

    private void setupNewCategory(View view){
        newCategoryText = (EditText) view.findViewById(R.id.edit_new_category);
        newCategoryText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    radioGroup.clearCheck();
                }
            }
        });
    }

    private void setupDialogFields(View view){
        String title = getString(R.string.title_select_category);
        getDialog().setTitle(title);

        TextView pageName = (TextView) view.findViewById(R.id.page_name);
        pageName.setText(page.getName());

        Button positive = (Button) view.findViewById(R.id.btn_positive);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOkClick();
            }
        });

        Button negative= (Button) view.findViewById(R.id.btn_negative);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancelClick();
            }
        });

    }

    private void handleOkClick(){
        WiKuoteDatabaseHelper db = WiKuoteDatabaseHelper.getInstance();
        Category category = null;

        int selected = radioGroup.getCheckedRadioButtonId();
        if (selected != -1){
            // existing category has been selected
            category = (Category) radioGroup.findViewById(selected).getTag();
        }

        else {
            // new category has been prompted
            String newCategoryTitle = newCategoryText.getText().toString();
            Category alreadyExistent = db.getCategoryFromTitle(newCategoryTitle);
            if (alreadyExistent!=null){
                category = alreadyExistent;
            }
            else if (db.isValidCategoryTitle(newCategoryTitle)){
                category = new Category(newCategoryTitle);
            }
        }

        if (category==null){
            Toast.makeText(getActivity(),R.string.err_invalid_input,Toast.LENGTH_SHORT).show();
            return;
        }

        WiKuoteDatabaseHelper.getInstance().movePageToCategory(page, category);
        getDialog().dismiss();
        WiKuoteNavUtils.getInstance().openQuoteFragmentSinglePage(page);
    }

    private void handleCancelClick(){
        getDialog().dismiss();
    }
}
