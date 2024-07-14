package com.github.ikovalyov

import org.testcontainers.utility.DockerImageName



object DockerImages {
    const val LOCALSTACK_IMAGE_NAME = "localstack/localstack:3"
}

interface LocalstackTestImages {
    companion object {
        val LOCALSTACK_IMAGE: DockerImageName = DockerImageName.parse("localstack/localstack:0.12.8")
        val LOCALSTACK_0_10_IMAGE: DockerImageName = LOCALSTACK_IMAGE.withTag("0.10.7")
        val LOCALSTACK_0_11_IMAGE: DockerImageName = LOCALSTACK_IMAGE.withTag("0.11.3")
        val LOCALSTACK_0_12_IMAGE: DockerImageName = LOCALSTACK_IMAGE.withTag("0.12.8")
        val LOCALSTACK_0_13_IMAGE: DockerImageName = LOCALSTACK_IMAGE.withTag("0.13.0")

        val LOCALSTACK_2_3_IMAGE: DockerImageName = LOCALSTACK_IMAGE.withTag("2.3")
        val AWS_CLI_IMAGE: DockerImageName = DockerImageName.parse("amazon/aws-cli:2.7.27")
    }
}