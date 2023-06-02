package com.example.campusin.domain.statistics.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kok8454@gmail.com on 2023-06-02
 * Github : http://github.com/perArdua
 */

@Getter
@Setter
@NoArgsConstructor
public class StatisticsIdResponse {

        private Long id;

        public StatisticsIdResponse(Long id) {
            this.id = id;
        }
}
