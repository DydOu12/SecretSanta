package com.example.dylan.randomnoel;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Mock
    private View mockView;

    @Test
    public void testGetTextColor_withCoulValueNull() {
        int textColor = Utils.getTextColor(null, mockView, 3);
        verify(mockView, never()).setBackgroundColor(anyInt());
        assertEquals(3, textColor);
    }

    @Test
    public void testGetTextColor_withCoulValueNotNull() {
        int textColor = Utils.getTextColor(0xFFFF0000, mockView, 3);

        verify(mockView).setBackgroundColor(0xFFFADBD8);
        assertEquals(3, textColor);
    }

    @Test
    public void testGetTextColor_withBlueGroupColor_usesLightTint() {
        int textColor = Utils.getTextColor(0xFF0000FF, mockView, 3);

        verify(mockView).setBackgroundColor(0xFFDCDBF7);
        assertEquals(3, textColor);
    }
}
