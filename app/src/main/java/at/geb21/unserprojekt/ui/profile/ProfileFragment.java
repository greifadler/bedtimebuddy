package at.geb21.unserprojekt.ui.profile;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.geb21.unserprojekt.R;
import at.geb21.unserprojekt.databinding.FragmentProfileBinding;
import at.geb21.unserprojekt.helpers.Helpers;
import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private ProfileViewModel profileViewModel;

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);


        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView tvName = binding.textProfileName;
        final TextView tvAge = binding.textProfileAge;
        final TextView tvGender = binding.textProfileGender;
        final TextView tvLight = binding.textLightThreshold;
        final Button editButton = binding.button;
        final Button editLightButton = binding.buttonLightTrashold;
        profileViewModel.getName().observe(getViewLifecycleOwner(), tvName::setText);
        profileViewModel.getAge().observe(getViewLifecycleOwner(), tvAge::setText);
        profileViewModel.getGender().observe(getViewLifecycleOwner(), tvGender::setText);
        profileViewModel.getGender().observe(getViewLifecycleOwner(), tvGender::setText);
        profileViewModel.getLightAmountLd().observe(getViewLifecycleOwner(), tvLight::setText);
        editButton.setOnClickListener(it -> {
            showPopup();
        });

        editLightButton.setOnClickListener(it -> {
            showDialogWithSpinner();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Shows a popup window with profile information and allows the user to edit and save the profile.
     */
    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_profile, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        Button btnSave = popupView.findViewById(R.id.btnSave);
        EditText editName = popupView.findViewById(R.id.editName);
        EditText editAge = popupView.findViewById(R.id.editAge);
        RadioGroup editGender = popupView.findViewById(R.id.editGender);

        editName.setText(profileViewModel.getName().getValue());
        editAge.setText(profileViewModel.getAge().getValue().replace(" years", ""));

        btnSave.setOnClickListener(v -> {
            try {
                String name = editName.getText().toString();
                int age = Integer.parseInt(editAge.getText().toString());
                String gender = ((RadioButton) popupView.findViewById(editGender.getCheckedRadioButtonId())).getText() +"";

                profileViewModel.saveProfile(name, age, gender);

            } catch (Exception e) {
                System.out.println(e);
            }
            popupWindow.dismiss();
        });

        Button btnCancel = popupView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        View decorView = requireActivity().getWindow().getDecorView();

        popupWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);
    }

    /**
     * Shows a dialog with a spinner for selecting the light threshold.
     */
    private void showDialogWithSpinner() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_choose_light_treshold);

        Spinner spinner = new Spinner(getContext());
        List<Integer> numberList = new ArrayList<>();
        for (int i = 0; i <= 1000; i++) {
            numberList.add(i);
        }
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, numberList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        builder.setView(spinner);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedNumber = (int) spinner.getSelectedItem();
            profileViewModel.saveLightAmount(selectedNumber);
        });

        spinner.setSelection(profileViewModel.getLightAmount());

        builder.setNegativeButton(R.string.cancle, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}