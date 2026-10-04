package com.cptm.ProjetoCPTM;
import com.cptm.ProjetoCPTM.application.*;
import com.cptm.ProjetoCPTM.persistence.NetworkRepository;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class TransactionFailureTest {
    @Test void failedPersistenceDoesNotPublishCommand() {
        NetworkRepository repository=mock(NetworkRepository.class);NetworkService service=new NetworkService(repository,JsonMapper.builder().build());service.initialize();
        doThrow(new IllegalStateException("database unavailable")).when(repository).save(any(),anyLong(),anyString(),anyString(),anyString());
        assertThatThrownBy(()->service.clock(new Commands.ClockInput(true,10),"test")).isInstanceOf(IllegalStateException.class);
        assertThat(service.export().running).isFalse();assertThat(service.version()).isZero();assertThat(service.export().timeScale).isEqualTo(5);
    }
}
