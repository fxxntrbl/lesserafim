# 1과제

> VPC -> Subnet 생성 (+ Routing Table 연결 & Subnet 연결) -> NAT Gateway 생성 -> Bastion 셍성 (+ IAM Role, Security Group) -> ECR -> EKS -> DocumentDB, ElastiCache, Secrets Manager

helm 설치하자

그리고 이름 다 주니까 똑바로 바꾸자

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

**--platform linux/amd64** 유의

## EKS

### IAM Policy 설정

```bash
eksctl utils associate-iam-oidc-provider --cluster <Cluster Name> --region ap-northeast-2 --approve
```

```bash
curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.5.4/docs/install/iam_policy.json
```

```bash
aws iam create-policy \
    --policy-name AWSLoadBalancerControllerIAMPolicy \
    --policy-document file://iam_policy.json
```

```bash
eksctl create iamserviceaccount \
    --cluster <Cluster Name> \
    --namespace kube-system \
    --name aws-load-balancer-controller \
    --attach-policy-arn arn:aws:iam::<Account ID>:policy/AWSLoadBalancerControllerIAMPolicy \
    --override-existing-serviceaccounts \
    --region ap-northeast-2 \
    --approve
```

### LBC 설치

```bash
helm repo add eks https://aws.github.io/eks-charts
```

```bash
helm repo update eks
```

```bash
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=<Cluster Name> \
  --set serviceAccount.create=false \
  --set region=ap-northeast-2 \
  --set vpcId=<VPC ID> \
  --set serviceAccount.name=aws-load-balancer-controller
```

다 깔고 확인하자

```bash
kubectl get deployment -n kube-system aws-load-balancer-controller
```

## 참고자료

- [그저 goat](https://theblackskirts.notion.site/1-ce89d70048064a1f9f60c8b53a21a5bb?pvs=4)
- [성민이 블로그](https://blog.isamin.kr/takanashi-hoshino/)
- [AWS Docs](https://aws.amazon.com/ko/blogs/containers/leverage-aws-secrets-stores-from-eks-fargate-with-external-secrets-operator/)
