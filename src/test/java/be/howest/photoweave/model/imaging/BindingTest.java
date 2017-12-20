package be.howest.photoweave.model.imaging;

import be.howest.photoweave.model.binding.Binding;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BindingTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCompareBase64Binding() throws IOException {
        //21.png
        String b64String =  "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAMAAAC67D+PAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRFAAAA////pdmf3QAAACFJREFUeNpiYAACRigBRmAOA4KDKckIlUeSYSBOG0CAAQAGUwAdqI6v1QAAAABJRU5ErkJggg==";
        Binding b64Binding = new Binding(b64String,"Test");
        System.out.println(b64Binding.getIntensityCount());
        assertEquals("Binding 21 heeft een intensiteit van 28",(long)b64Binding.getIntensityCount(),(long)28);
    }

}
