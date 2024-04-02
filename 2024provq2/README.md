# 2과제

> VPC -> Bastion -> Frontend CodePipeline (CodeCommit -> S3) -> CloudFront -> Backend CodePipeline (CodeCommit -> CodeBuild -> CodeDeploy) -> ECS -> ALB -> CloudFront

**암호화** **EIP** **그 외 표현이 ㅈㄴ 추상적인 기타 등등**

## VPC

하라는대로 하도록 하자

## Bastion

**니가 제일 잘 까먹는거**

IAM Role, Security, Policy, **Elastic IP**

```bash
sudo yum update -y
sudo yum install -y jq curl wget git docker —allowerasing
sudo yum list java*jdk-devel # java jdk list
sudo yum install -y java-1.8.0-openjdk-devel.x86_64 # 혹시 모르니!!
```

아래는 도커 관련

```bash
sudo systemctl start docker # daemon start
sudo usermod -aG docker ec2-user # no need for sudo
```

## Frontend

> **이 단계 이후로는 IAM Role, 권한 설정에 특히 유의하도록 하자** 그리고 문제지에 나와있는 **암호화**.. 로깅... 뭐 그런거 ㅇㅇ

### CodeCommit

`git clone` 해서 선수 배포파일 업로드하자

참고로 git clone 할 때 IAM 유저 만들어야 함

### S3 버킷 생성

문제에 나와있는 대로 **ACL**, **퍼블릭 액세스** 등 잘 체크하고 만들어주자

이후 아래 `정적 웹 사이트 호스팅` 을 활성화하자

만약에 활성화했는데 액세스 안되면 버킷 정책 추가하고 퍼블릭 액세스 ㄱㄱ

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "<BUCKET_ARN>/*"
    }
  ]
}
```

### CodePipeline

알잘딱 ㄱ + `배포하기 전에 파일 압축 풀기`

서비스 역할 하라는대로 하는데 권한 잘 주자

## Backend

### CodeCommit

`git clone` 해서 선수 배포파일 업로드하자

얘도 역시 git clone 할 때 IAM 유저 만들어야 함

**Dockerfile** 에 **curl**이 똑바로 되어있는지 보자 (health check 셋팅할때 짜증남)

```Dockerfile
RUN apt update && apt install -y curl
```

### ECR

**권한 확인** **IMMUTABLE** **암호화** **스캐닝**

### CodeBuild

- `buildspec.yml`

```yaml
version: 0.2

phases:
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - export AWS_ACCOUNT_ID=<ACCOUNT ID>
      - export IMAGE_REPO_NAME=<REPO NAME>
      - export IMAGE_TAG=latest
      - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - docker build -t $IMAGE_REPO_NAME:$IMAGE_TAG .
      - docker tag $IMAGE_REPO_NAME:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing the Docker image...
      - docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
      - printf '[{"name":"<REPO NAME>","imageUri":"%s"}]' $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG > imagedefinitions.json

artifacts:
  files:
    - imagedefinitions.json
    - appspec.yml
```

만약에... ECR을 안 쓴다면 뭐 똥꼬쇼 ㄱ

추가적으로 VPC 설정이 잘 되는지 확인하자

`VPC 설정 검증` ㄱㄱ

그 외 로깅이나 env 여기서 설정 ㄱㄱ (예시로 buildspec에 있는 애들...? 잘 몰루)

### ECS

클러스터 만드셈

로깅 이런거 잘 확인하자

### ECS (1) - Task Definiton

하라는대로 만들자

참고로 여기서 말하는 컨테이너 이름 맘대로 줘도 댐 `appspec` 이랑 일치하면 됌

- `health check`

```bash
CMD-SHELL, curl -f http://localhost:8080/health || exit 1
```

### ECS (2) - ALB, Target Group

**보안 그룹** 에 유의하자 (80 443 anyopen)

상식이지만 ALB는 `internet-facing`, 퍼블릭 서브넷에 던지자

### ECS (3) - Service

끼워맞추자!

배포 옵션에서 **CodeDeploy** 로 바꾸는거 잊지 ㄴㄴ

네트워킹 옵션도...

### CodeDeploy

- `appspec.yml`

```yml
version: 0.0
Resources:
  - TargetService:
      Type: AWS::ECS::Service
      Properties:
        TaskDefinition: "<TASKDEF ARN NAME>"
        LoadBalancerInfo:
          ContainerName: "<TASKDEF CONTAINER NAME>"
          ContainerPort: 8080
```

### CodePipeline

권한 설정에 유의하자

끼워 맞추면 끝남

자잘한 셋팅에 유의하시고

### 중간 점검

니가 만약 여기까지 잘 따라왔으면 alb endpoint에 요청을 날렸을때 랄끼얏호우하고 값을 뱉을거임

안되면 **Target Group** **ECS Service** **CodeDeploy** 를 확인해보자

### CloudFront

이미 배포한거에 원본을 추가하자

![origin](https://github.com/fxxntrbl/lesserafim/blob/main/2024provq2/images/origin.png)

![pattern](https://github.com/fxxntrbl/lesserafim/blob/main/2024provq2/images/pattern.png)

## 참고자료

- [그저goat](https://theblackskirts.notion.site/2-2d57aa686e704589b4479fd25375930d?pvs=4)
- [CodeCommit 403 에러](https://velog.io/@on_cloud/AWS-CodeCommit-Error)
- [유튭](https://youtu.be/8iEw58P_0z8?si=XYo3lyDPmnmCtlzH)
