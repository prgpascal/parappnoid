package com.prgpascal.parappnoid.application.fragments.dialogs;

/**
 * Public Dialog Interface.
 * Contains all the methods that must be implemented by Activities that use AlertDialogs.
 */
public interface MyAlertDialogInterface {
    void doPositiveClick(int dialogType, char[] result);
    void doNegativeClick(int dialogType, char[] result);
    void showNewDialog(int dialogType);
}
