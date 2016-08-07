package com.prgpascal.parappnoid.application.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.prgpascal.parappnoid.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by prgpascal on 07/08/2016.
 */
public class ChangelogDialogFragment extends DialogFragment {
    public static final String TAG = "changelog_dialog_fragment";
    private static final String TAG_CHANGELOG = "changelog";
    private static final String TAG_TITLE = "title";

    public static ChangelogDialogFragment newInstance(String title, HashMap<String, String> changelog) {
        ChangelogDialogFragment frag = new ChangelogDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_CHANGELOG, changelog);
        args.putSerializable(TAG_TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final HashMap<String, String> changelog = (HashMap<String, String>) getArguments().getSerializable(TAG_CHANGELOG);
        final String title = getArguments().getString(TAG_TITLE);

        View parent = getActivity().getLayoutInflater().inflate(R.layout.fragment_changelog, null);
        TextView textView = (TextView) parent.findViewById(R.id.textView);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            Iterator ite = changelog.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry entry = (Map.Entry) ite.next();

                // Title
                stringBuilder.append("<b><i>");
                stringBuilder.append(entry.getKey()).append(":");
                stringBuilder.append("</i></b>");
                stringBuilder.append("<br/>");

                // Description
                stringBuilder.append(entry.getValue());
                stringBuilder.append("<br/><br/>");
            }

        } catch (NullPointerException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("No changes!"); //TODO add this to strings.xml
        }

        textView.setText(Html.fromHtml(stringBuilder.toString()));

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(parent)
                .setPositiveButton(R.string.ok, null)
                .create();
    }

}
