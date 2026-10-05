package com.cptm.ProjetoCPTM;

import com.cptm.ProjetoCPTM.security.DesktopAccessFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
        "spring.datasource.url=jdbc:h2:mem:desktop;DB_CLOSE_DELAY=-1", "rail.scheduler.enabled=false",
        "rail.desktop.watch-parent=false", "rail.desktop.token=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"})
@ActiveProfiles("desktop")
@AutoConfigureMockMvc
class DesktopSecurityTest {
    private static final String TOKEN="0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    @Autowired MockMvc mvc;
    @Test void noCapabilityDeniesEvenPublicAssetsAndAuthenticatedSessions() throws Exception {
        mvc.perform(get("/")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/network").with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
    }
    @Test void localCapabilityEntersWithoutPassword() throws Exception {
        mvc.perform(get("/api/v1/session").header(DesktopAccessFilter.HEADER,TOKEN))
            .andExpect(status().isOk()).andExpect(jsonPath("$.desktop").value(true))
            .andExpect(jsonPath("$.username").value("local")).andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }
    @Test void wrongCapabilityRemoteAddressAndForeignOriginAreDenied() throws Exception {
        mvc.perform(get("/").header(DesktopAccessFilter.HEADER,"0".repeat(64))).andExpect(status().isForbidden());
        mvc.perform(get("/").header(DesktopAccessFilter.HEADER,TOKEN).with(request->{request.setRemoteAddr("192.168.1.2");return request;})).andExpect(status().isForbidden());
        mvc.perform(get("/").header(DesktopAccessFilter.HEADER,TOKEN).header("Origin","https://evil.test")).andExpect(status().isForbidden());
    }
    @Test void desktopCommandsStillRequireCsrf() throws Exception {
        String body="{\"running\":false,\"timeScale\":1}";
        mvc.perform(post("/api/v1/clock").header(DesktopAccessFilter.HEADER,TOKEN).contentType("application/json").content(body)).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/clock").header(DesktopAccessFilter.HEADER,TOKEN).with(csrf()).contentType("application/json").content(body)).andExpect(status().isOk());
    }
    @Test void shutdownAlsoRequiresCsrf() throws Exception {
        mvc.perform(post("/desktop/shutdown").header(DesktopAccessFilter.HEADER,TOKEN)).andExpect(status().isForbidden());
    }
    @Test void capabilityMustContain256Bits() {
        assertThatThrownBy(()->new DesktopAccessFilter("short")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->new DesktopAccessFilter(null)).isInstanceOf(IllegalStateException.class);
    }
}
