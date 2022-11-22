package com.nagesoft.club.zone;

import com.nagesoft.club.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class ZoneService {
    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {

//        if (zoneRepository.count() == 0) {
//            Resource resource = new ClassPathResource("zones_kr.csv");
//            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
//                    .map(line -> {
//                        String[] split = line.split(",");
//                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
//                    }).collect(Collectors.toList());
//            zoneRepository.saveAll(zoneList);
//        }


        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");

            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < 85) {
                sb.append(br.readLine());
                sb.append("\n");
                i++;
            }
            String s = sb.toString();
            String[] zones = s.split("\n");

            List<Zone> zoneList = Arrays.stream(zones).map(line -> {
                String[] split = line.split(",");
                return Zone.builder()
                        .city(split[0])
                        .localNameOfCity(split[1])
                        .province(split[2])
                        .build();
            }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }

    }
}
