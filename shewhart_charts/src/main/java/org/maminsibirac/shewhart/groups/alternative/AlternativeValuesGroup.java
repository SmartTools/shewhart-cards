package org.maminsibirac.shewhart.groups.alternative;

import static org.maminsibirac.shewhart.utils.ValidationUtils.checkOnNull;

// ToDo : write error messages.
public class AlternativeValuesGroup<TKey extends Comparable<TKey>> implements AlternativeValuesControlGroup<TKey> {
    private TKey key;
    private int controlNumber;
    private int incorrectNumber;

    private AlternativeValuesGroup(TKey key) {
        checkOnNull(key, "");
        this.key = key;
    }

    private AlternativeValuesGroup(TKey key, int controlNumber, int incorrectNumber) {
        checkOnNull(key, "");
        checkOnNull(controlNumber, "");
        checkOnNull(incorrectNumber, "");

        this.key = key;
        this.controlNumber = controlNumber;
        this.incorrectNumber = incorrectNumber;
    }

    @Override
    public void addIncorrectNumber(int incorrectNumber) {
        checkOnNull(incorrectNumber, "");
        this.incorrectNumber = incorrectNumber;
    }

    @Override
    public int getIncorrectNumber() {
        return this.incorrectNumber;
    }

    @Override
    public TKey getKey() {
        return key;
    }

}
