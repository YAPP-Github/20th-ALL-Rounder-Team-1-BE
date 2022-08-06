echo "##########(ECR 에 로그인)##########"
aws ecr-public get-login-password --region us-east-1 --profile weekand-server |
docker login --username AWS --password-stdin public.ecr.aws/s4x9g1h9 || exit 1
echo "##########(Gradle build)##########"
./gradlew -x test clean build || exit 2
echo "##########(이미지 build)##########"
docker build --platform amd64 -t weekand-server . || exit 3
echo "##########(이미지 tagging)##########"
docker tag weekand-server:latest public.ecr.aws/s4x9g1h9/weekand-server:latest || exit 4
echo "##########(이미지 AWS ECR 에 push)##########"
docker push public.ecr.aws/s4x9g1h9/weekand-server:latest || exit 5
echo "##########(이미지 배포 완료)##########"
