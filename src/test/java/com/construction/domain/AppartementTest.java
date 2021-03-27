package com.construction.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.construction.web.rest.TestUtil;

public class AppartementTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Appartement.class);
        Appartement appartement1 = new Appartement();
        appartement1.setId(1L);
        Appartement appartement2 = new Appartement();
        appartement2.setId(appartement1.getId());
        assertThat(appartement1).isEqualTo(appartement2);
        appartement2.setId(2L);
        assertThat(appartement1).isNotEqualTo(appartement2);
        appartement1.setId(null);
        assertThat(appartement1).isNotEqualTo(appartement2);
    }
}
