package com.example.dylan.randomnoel;

import android.graphics.Color;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
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
        try(MockedStatic<Color> mockColor = mockStatic(Color.class)) {

            mockColor.when(() -> Color.red(anyInt())).thenReturn(1);
            mockColor.when(() -> Color.green(anyInt())).thenReturn(1);
            mockColor.when(() -> Color.blue(anyInt())).thenReturn(1);

            int textColor = Utils.getTextColor(0xFFFF0000, mockView, 3);
            verify(mockView).setBackgroundColor(0xFFFF0000);
            assertEquals(Color.WHITE, textColor);
        }
    }

    @Test
    public void testGetTextColor_withCoulValueNotNullAndTextEnoughBrightness() {
        try(MockedStatic<Color> mockColor = mockStatic(Color.class)) {

            mockColor.when(() -> Color.red(anyInt())).thenReturn(1000);
            mockColor.when(() -> Color.green(anyInt())).thenReturn(1000);
            mockColor.when(() -> Color.blue(anyInt())).thenReturn(1000);

            int textColor = Utils.getTextColor(0xFFFF0000, mockView, 3);
            verify(mockView).setBackgroundColor(0xFFFF0000);
            assertEquals(3, textColor);
        }
    }
}
