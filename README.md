# Regulatory Structure

Essa aplicação provê algumas classes e configurações gerais, para o uso nas aplicações de regulatórios. Comumente, as
aplicações possuem uma API para receber e enviar arquivos, que serão armazenados no storage da AWS (S3)

Uma parte importante das aplicações é, justamente, o uso e testes de acessos aos buckets, onde podemos testar o recebimento,
além de possíveis falhas no processo de acesso aos buckets.


## Testes Locais dos Buckets

Para ter a estrutura básica de acesso ao Bucket, basta utilizar a Interface *BucketClient* e as classes *S3ClientFileSystem*,
*AwsProperties* e *S3Config*. É importante ter a interface *BucketClient* na aplicação pois, ela vai facilitar possíveis mudanças,
na hora de executar os testes locais.

Para fazer os testes locais, adicione a classe *MiniIoClientFileSystem* e crie um container docker para subir o miniIo utilizando a
string padrão mesmo.

`docker run --name s3Files -d -p 9000:9000 -p 9001:9001 quay.io/minio/minio server /data --console-address ":9001"`

Uma vez que o container subir, crie o bucket, no miniIo, acessando o serviço via `http://localhost:9001`. Por padrão, o usuário e senha é `minioadmin` 
Também crie as chaves de acesso (AccessKey, SecretKey) e verifique a região do bucket

Uma vez que o bucket foi criado, modifique o nome do atributo injetado, na classe que utiliza o serviço de acesso ao bucket. Exemplo:
Por padrão, utilizamos o `s3ClientFileSystem` para acessar o bucket na aws. Para acessar o minio, precisamos utilizar o `miniIoClientFileSystem`, da seguinte forma

```java
@Service
@RequiredArgsConstructor
public final class FileActionsUseCase {
  private final BucketClient s3ClientFileSystem;
}
```

No caso acima, para utilizar a implementação do `MiniIoClientFileSystem`, fazemos a substituição do nome do bean, da seguinte forma:

```java
@Service
@RequiredArgsConstructor
public final class FileActionsUseCase {
  private final BucketClient miniIoClientFileSystem;
}
```

Dessa forma, não é necessário fazer nenhuma modificação na estrutura da classe ou, nas chamadas de método pois, as assinaturas dos métodos são as mesmas.

> Atenção: Para subir as aplicações, é preciso remover as configurações para os testes locais ou, não deixar que o código de teste suba para o repositório