package no.itera.mad.stats;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.itera.mad.stats.model.Statistics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StatsRepositoryTest {

  @Autowired
  private StatsRepository statsRepository;

  @Test
  public void shouldSaveAndRetrieveStatistics() {
    statsRepository.save(new Statistics("service", "topic", "key", "value"))
      .then()
      .block();

    Statistics stats = statsRepository.findWithTailableCursorBy()
        .blockFirst();

    assertThat(stats, not(nullValue()));
  }

}
