package com.prgpascal.parappnoid.application.fragments.dialogs;

/**
 * Interface that define all the methods that must be implemented by the Activities that use AlertDialogs.
 */
public interface MyAlertDialogInterface {
    void doPositiveClick(int dialogType, char[] result);

    void doNegativeClick(int dialogType, char[] result);

    void showNewDialog(int dialogType);
}
