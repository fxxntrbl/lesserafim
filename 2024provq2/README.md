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

## 참고자료

- [그저goat](https://theblackskirts.notion.site/2-2d57aa686e704589b4479fd25375930d?pvs=4)
- [CodeCommit 403 에러](https://velog.io/@on_cloud/AWS-CodeCommit-Error)
