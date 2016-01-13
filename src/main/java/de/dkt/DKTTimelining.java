package de.dkt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import eu.freme.broker.EDocumentStorageConfig;
import eu.freme.broker.ELuceneConfig;
import eu.freme.broker.EOpenNLPConfig;
import eu.freme.broker.EWekaConfig;
import eu.freme.broker.FremeCommonConfig;
import eu.freme.broker.tools.StarterHelper;
import eu.freme.common.FREMECommonConfig;
import eu.freme.eservices.eentity.EEntityConfig;
import eu.freme.eservices.elink.ELinkConfig;
import eu.freme.eservices.epublishing.EPublishingConfig;
import eu.freme.eservices.pipelines.api.PipelineConfig;
import eu.freme.i18n.api.EInternationalizationConfig;

@SpringBootApplication
@Import({ DKTTimeliningConfig.class, FremeCommonConfig.class, EEntityConfig.class, ELinkConfig.class,
	EPublishingConfig.class, FREMECommonConfig.class,
	PipelineConfig.class, EInternationalizationConfig.class,
	EWekaConfig.class, ELuceneConfig.class, EDocumentStorageConfig.class, EOpenNLPConfig.class })
@Profile("broker")
public class DKTTimelining {
    public static void main(String[] args) {
		String[] newArgs = StarterHelper.addProfile(args, "broker");

        SpringApplication.run(DKTTimelining.class, newArgs);
    }
}