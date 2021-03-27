package com.construction.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.construction.web.rest.TestUtil;

public class EtageTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Etage.class);
        Etage etage1 = new Etage();
        etage1.setId(1L);
        Etage etage2 = new Etage();
        etage2.setId(etage1.getId());
        assertThat(etage1).isEqualTo(etage2);
        etage2.setId(2L);
        assertThat(etage1).isNotEqualTo(etage2);
        etage1.setId(null);
        assertThat(etage1).isNotEqualTo(etage2);
    }
}
