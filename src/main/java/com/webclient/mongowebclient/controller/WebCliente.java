package com.webclient.mongowebclient.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.webclient.mongowebclient.document.Conection;
import com.webclient.mongowebclient.repository.ConectionDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;


@Slf4j
@RestController
@RequestMapping("/client")
@Component
@CrossOrigin(allowedHeaders = "*", originPatterns = "*")
public class WebCliente {


    @Autowired
    private ConectionDao conectionDao;


    @GetMapping("/all")
    public Flux<Conection> getAll() {
        return conectionDao.findAll();
    }


    @PostMapping("/save")
    public Mono<ResponseEntity<Conection>> getWebClient(@RequestBody Conection conection) {


        WebClient webClient = WebClient.builder()
                .baseUrl(conection.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();//"text/plain", "text/html","application/octet-stream"


        Flux<Object> string = webClient.get()
                // .uri(conection.getHost())
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(buffer -> {
                    String stringg = buffer.toString(Charset.forName("UTF-8"));
                    DataBufferUtils.release(buffer);
                    return stringg;
                });


        Flux<String> string2 = string.map(s -> s.toString());
        Mono<String> string3 = string2.next();


        log.info("{}", string3.block());


        conection.setText(string3.block());
        conectionDao.save(conection).subscribe();
        return Mono.just(new ResponseEntity<>(conection, HttpStatus.OK));


    }


    @GetMapping("/data")
    public Flux<ResponseEntity<Conection>> getData() {

        return conectionDao.findAll().map(conection -> new ResponseEntity(conection, HttpStatus.OK));

    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Conection>> downloaded(@PathVariable("id") String id) {

        Flux<Conection> conection = conectionDao.findAll();

        Mono<Conection> conectionMono = conection.filter(conection1 ->
                conection1.getId().equals(id)).next();

        return conectionMono.map(conection1 -> new ResponseEntity(conection1, HttpStatus.OK));

    }


    @GetMapping("/des/{fileName}")
    public Mono<ResponseEntity<String>> getDownloadData(
            @PathVariable String fileName) throws Exception {

        Flux<Conection> conection = conectionDao.findAll();
        Mono<Conection> conectionMono = conection.filter(conection1 ->
                conection1.getId().equals(fileName)).next();

        String host = conectionMono.block().getHost();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(conectionMono.block());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-disposition", "attachment; filename= " + host + ".txt");
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);


        return Mono.just(new ResponseEntity<>(json, responseHeaders, HttpStatus.OK));

    }


    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Conection>> delete(@PathVariable("id") String id) {

        return conectionDao.findById(id)
                .flatMap(conection -> conectionDao.delete(conection)
                        .then(Mono.just(new ResponseEntity<>(conection, HttpStatus.OK))))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }


}



/*
---------------------------------------------------------------------------------------------------------------------

  Mono <String> response = webClient.get()
                .uri("/100MB.bin")
                .retrieve()
               // handle status
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.error("Error endpoint with status code {}", clientResponse.statusCode());
                    try {
                        throw new ApiWebClientException("HTTP Status 500 error");  // throw custom exception
                    } catch (ApiWebClientException e) {
                        throw new RuntimeException(e);
                    }

                })

                .bodyToFlux(DataBuffer.class)
                .map(buffer -> {
                    String string = buffer.toString(Charset.forName("UTF-8"));
                    DataBufferUtils.release(buffer);
                    return string;
                });----


    RestTemplate plantilla = new RestTemplate();
        String resultado = plantilla.getForObject("https://speed.hetzner.de/100MB.bin", String.class);
        System.out.println(resultado);

 */


/*
---------------------------------------------------------------------------------------------------------------------


        //.bodyToFlux(DataBuffer.class)

               .map(buffer -> {
                    String stringg = buffer.toString(Charset.forName("UTF-8"));
                    DataBufferUtils.release(buffer);
                    return stringg;
                });


// res.subscribe(System.out::println);


//IMPORTANTE:
        File file = new File("/Users/oscartabora/Desktop/file/dev2.text");
        Path path = file.toPath();
        DataBufferUtils.write(res, path, StandardOpenOption.CREATE).share().block();


   return Flux.just(ResponseEntity.ok()
                .allow(HttpMethod.GET)

                .body(res));


 */




    /*public  Mono<ResponseEntity<?>> getWebClient(@RequestBody Conection conection) {

        WebClient webClient = WebClient.builder()
                .baseUrl(conection.getUri())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=ISO-8859-1")
                .defaultHeader(HttpHeaders.ACCEPT, "text/html;charset=ISO-8859-1")
                .build();


        Flux<DataBuffer> res = webClient.get()
                .uri(conection.getUri())
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .doOnComplete(() -> log.info("{}", 1 + " - File downloaded successfully"));


        Flux<String> flux = res.map(dataBuffer -> {
            String s = dataBuffer.toString(StandardCharsets.ISO_8859_1);
            return s;
        });


        Mono<String> mono = flux.collectList().map(strings -> {
            StringBuilder sb = new StringBuilder();
            for (String s : strings) {
                sb.append(s);
            }
            return sb.toString();
        });


        String s = mono.block();


        conectionDao.save(conection).subscribe(conectio -> log.info("{}", conectio));


        return Mono.just(new ResponseEntity<>(, HttpStatus.OK));


    }*/




/*




      //  Mono<Object> mono = webClient2.get().exchange()
        //        .doOnSuccess(clientResponse -> System.out.println("clientResponse.headers() = " + clientResponse.headers().contentType())).map(clientResponse -> clientResponse.bodyToMono(String.class));
                //.flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

        webClient2
                .get()
                .uri(conection.getHost()).headers(HttpHeaders::getHost).exchange();
                //.headers(h -> h.get(new ArrayList<String>().add("Content Type".toLowerCase(Locale.ROOT)))).exchange()
                //.map(clientResponse -> clientResponse.bodyToMono(String.class));

    Mono<String> x = webClient2.head().uri(conection.getHost()).header("Content-Type", "application/json").exchange().flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
          //  .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

        System.out.println(">>>>>>>>> xxxxx  " + x.block());
 */


   /* @GetMapping("/des/{fileName}")
    public Mono<Resource> getFile(@PathVariable String fileName) {
        return conectionDao.findById(fileName)
                .map(name -> new FileSystemResource(name.getText()));




                 // responseHeaders.set("charset", "utf-8");
        // responseHeaders.setContentType(MediaType.valueOf("text/html"));
        //----responseHeaders.setContentLength(output.length);
        // responseHeaders.set("Content-disposition", "attachment; filename=filename.txt");

    }

        // Mono<String> mono = conectionMono.map(x -> x.getText());


        //  String s = mono.block();


        //String regData = s;
        // byte[] output = regData.getBytes();

    */



/*


  /* Mono<String> mono = flux.collectList().map(strings -> {
            StringBuilder sb = new StringBuilder();
            for (String s : strings) {
                sb.append(s);
            }
            return sb.toString();
        });*/

// System.out.println(string3);


      /*  Mono<String> flux = res.map(dataBuffer -> {
            String s = dataBuffer.toString(StandardCharsets.ISO_8859_1);
            return s;
        });*/




     /*   Mono<DataBuffer> res = webClient.get()
                .uri(conection.getHost())
                .retrieve()
                .bodyToMono(DataBuffer.class);*/