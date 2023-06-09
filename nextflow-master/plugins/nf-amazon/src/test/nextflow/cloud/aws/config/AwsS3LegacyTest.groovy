/*
 * Copyright 2020-2022, Seqera Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nextflow.cloud.aws.config


import nextflow.util.Duration
import nextflow.util.MemoryUnit
import spock.lang.Specification
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class AwsS3LegacyTest extends Specification{

    def 'should normalize aws config' () {

        given:
        def config = [uploadMaxThreads: 5, uploadChunkSize: 1000, uploadStorageClass: 'STANDARD' ]

        when:
        def norm = AwsS3Legacy.normalizeAwsClientConfig(config)

        then:
        norm.upload_storage_class == 'STANDARD'
        norm.upload_chunk_size == '1000'
        norm.upload_max_threads == '5'

        when:
        config.uploadChunkSize = '10MB'
        then:
        AwsS3Legacy.normalizeAwsClientConfig(config).upload_chunk_size == '10485760'

        when:
        config.uploadChunkSize = '1024'
        then:
        AwsS3Legacy.normalizeAwsClientConfig(config).upload_chunk_size == '1024'

        when:
        config.uploadChunkSize = new MemoryUnit('2 MB')
        then:
        AwsS3Legacy.normalizeAwsClientConfig(config).upload_chunk_size == '2097152'

        when:
        config.uploadRetrySleep = '10 sec'
        then:
        AwsS3Legacy.normalizeAwsClientConfig(config).upload_retry_sleep == '10000'

        when:
        config.uploadRetrySleep = Duration.of('5 sec')
        then:
        AwsS3Legacy.normalizeAwsClientConfig(config).upload_retry_sleep == '5000'
    }

}
