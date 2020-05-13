require "google/apis/admob_v1"
require "googleauth"
require "googleauth/stores/file_token_store"
require "fileutils"

OOB_URI = "urn:ietf:wg:oauth:2.0:oob".freeze
APPLICATION_NAME = "AdMob API Ruby Network Report".freeze
CREDENTIALS_PATH = "credentials.json".freeze
# The file token.yaml stores the user's access and refresh tokens, and is
# created automatically when the authorization flow completes for the first
# time.
TOKEN_PATH = "token.yaml".freeze
SCOPE = 'https://www.googleapis.com/auth/admob.report'

##
# Ensure valid credentials, either by restoring from the saved credentials
# files or intitiating an OAuth2 authorization. If authorization is required,
# the user's default browser will be launched to approve the request.
#
# @return [Google::Auth::UserRefreshCredentials] OAuth2 credentials
def authorize
  client_id = Google::Auth::ClientId.from_file CREDENTIALS_PATH
  token_store = Google::Auth::Stores::FileTokenStore.new file: TOKEN_PATH
  authorizer = Google::Auth::UserAuthorizer.new client_id, SCOPE, token_store
  user_id = "default"
  credentials = authorizer.get_credentials user_id
  if credentials.nil?
    url = authorizer.get_authorization_url base_url: OOB_URI
    puts "Open the following URL in the browser and enter the " \
         "resulting code after authorization:\n" + url
    code = gets
    credentials = authorizer.get_and_store_credentials_from_code(
        user_id: user_id, code: code, base_url: OOB_URI
    )
  end
  credentials
end

# Initialize the API
admob_service = Google::Apis::AdmobV1::AdMobService.new
admob_service.client_options.application_name = APPLICATION_NAME
admob_service.authorization = authorize

account_response = admob_service.list_accounts
me = account_response.account[0].name

puts "Account(s):"
puts "No files found" if account_response.account.empty?

account_response.account.each do |account|
  puts "#{account.name}"
  puts "#{account.publisher_id}"
  puts "#{account.reporting_time_zone}"
end

request = Google::Apis::AdmobV1::GenerateNetworkReportRequest.new(
    report_spec: Google::Apis::AdmobV1::NetworkReportSpec.new(
        date_range: Google::Apis::AdmobV1::DateRange.new(
            start_date: Google::Apis::AdmobV1::Date.new(day: 1, month: 1, year: 2020),
            end_date: Google::Apis::AdmobV1::Date.new(day: 1, month: 2, year: 2020)),
        dimensions: ['APP'],
        metrics: ['ESTIMATED_EARNINGS', 'CLICKS'])
)

puts "", "Request:"
puts JSON.parse(request.to_json)

begin
  request_options = admob_service.request_options.clone
  request_options.skip_deserialization = true
  response = admob_service.generate_network_report(
      me,
      request,
      options: request_options
  )
  puts "", "Response:"
  MultiJson.load(response).each do |row|
    p_row = Google::Apis::AdmobV1::GenerateNetworkReportResponse.new()
    Google::Apis::AdmobV1::GenerateNetworkReportResponse::Representation
        .new(p_row)
        .from_hash row

    if p_row.instance_variable_defined?(:@header)
      range = p_row.header.date_range
      puts "header:",
           "  range: #{range.start_date.year}/#{range.start_date.month}/#{range.start_date.day}" +
               " - #{range.end_date.year}/#{range.end_date.month}/#{range.end_date.day}",
           "  currency: #{p_row.header.localization_settings.currency_code}"

    elsif p_row.instance_variable_defined?(:@row)
      puts "row:"
      p_row.row.dimension_values.each do |dimension, value|
        print "  #{dimension}: [#{value.value}, #{value.display_label}],"
      end
      p_row.row.metric_values.each do |metric, value|
        print " #{metric}: #{value.micros_value}#{value.double_value}#{value.integer_value}, "
      end
      puts ""
    elsif p_row.instance_variable_defined?(:@footer)
      puts "footer:",
           "  matching_row_count: #{p_row.footer.matching_row_count}"
    else
      puts "Unknow element"
    end
  end
rescue Google::Apis::ClientError => e
  puts e
  puts "Status Code: #{e.status_code}"
  puts "Response Body: #{e.body}"
end

