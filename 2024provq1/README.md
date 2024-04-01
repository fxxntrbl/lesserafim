# 1과제

> VPC -> Subnet 생성 (+ Routing Table 연결 & Subnet 연결) -> NAT Gateway 생성 -> Bastion 셍성 (+IAM Role, Security Group) -> ECR -> EKS -> DocumentDB, ElastiCache, Secrets Manager

## VPC

하라는대로 하도록 하자

### Protected Subnet

하라는대로 서브넷 만들고 라우팅테이블 만들고 NAT 게이트웨이 만들자

## Bastion

**니새끼가 제일 자주 까먹는거**
IAM Role, Security Group 셋팅 하자

```bash
sudo yum update -y
sudo yum install -y awscli curl jq golang docker
```

## ECR

뭐 알잘딱 ㄱ

## EKS
