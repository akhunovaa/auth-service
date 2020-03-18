package com.botmasterzzz.auth.config;


import com.botmasterzzz.auth.provider.VkOAuth2Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.spring.properties.EncryptablePropertyPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@ComponentScan("com.botmasterzzz.auth")
@EnableAsync
@EnableWebMvc
@Configuration
public class ApplicationConfig {

    private static List<String> clients = Arrays.asList("google", "facebook", "vk");

    @Value("${oauth2.google.client.id}")
    private String googleClientId;
    @Value("${oauth2.google.client.secret}")
    private String googleClientSecret;
    @Value("${oauth2.google.redirectUriTemplate}")
    private String googleRedirectUriTemplate;
    @Value("${oauth2.facebook.client.id}")
    private String facebookClientId;
    @Value("${oauth2.facebook.client.secret}")
    private String facebookClientSecret;
    @Value("${oauth2.facebook.redirectUriTemplate}")
    private String facebookRedirectUriTemplate;
    @Value("${oauth2.vk.client.id}")
    private String vkClientId;
    @Value("${oauth2.vk.client.secret}")
    private String vkClientSecret;
    @Value("${oauth2.vk.redirectUriTemplate}")
    private String vkRedirectUriTemplate;

    @Bean
    @DependsOn("configurationEncryptor")
    public static EncryptablePropertyPlaceholderConfigurer propertyEncodedPlaceholderConfigurer() {
        EncryptablePropertyPlaceholderConfigurer encryptablePropertyPlaceholderConfigurer = new EncryptablePropertyPlaceholderConfigurer(configurationEncryptor());
        encryptablePropertyPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        encryptablePropertyPlaceholderConfigurer.setLocation(new ClassPathResource("application.properties"));
        return encryptablePropertyPlaceholderConfigurer;
    }

    @Bean
    @DependsOn("environmentVariablesConfiguration")
    public static StandardPBEStringEncryptor configurationEncryptor() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setConfig(environmentVariablesConfiguration());
        return standardPBEStringEncryptor;
    }

    @Bean
    public static EnvironmentStringPBEConfig environmentVariablesConfiguration() {
        EnvironmentStringPBEConfig environmentStringPBEConfig = new EnvironmentStringPBEConfig();
        environmentStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        environmentStringPBEConfig.setPassword("710713748");
        return environmentStringPBEConfig;
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        return multipartResolver;
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public JsonDeserializer jsonDeserializer() {
        return new JsonDeserializer() {
            @Override
            public Object deserialize(String topic, byte[] data) {
                return null;
            }
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new InMemoryClientRegistrationRepository(registrations);
    }


    private ClientRegistration getRegistration(String client) {

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .redirectUriTemplate(googleRedirectUriTemplate)
                    .build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(facebookClientId)
                    .clientSecret(facebookClientSecret)
                    .redirectUriTemplate(facebookRedirectUriTemplate)
                    .build();
        }
        if (client.equals("vk")) {
            return VkOAuth2Provider.VK.getBuilder(client)
                    .clientId(vkClientId)
                    .clientSecret(vkClientSecret)
                    .redirectUriTemplate(vkRedirectUriTemplate)
                    .build();
        }
        return null;
    }


}
