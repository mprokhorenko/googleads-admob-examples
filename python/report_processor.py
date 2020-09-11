import csv


def to_plain_view(response):
    result = []

    for report_line in response:
        if report_line.get('row'):
            row = report_line.get('row')
            dm = {}
            if row.get('dimensionValues'):
                for key, value in row.get('dimensionValues').items():
                    if value.get('value') and value.get('displayLabel'):
                        dm.update({key: value.get('value')})
                        dm.update({key + '_NAME': value.get('displayLabel')})
                    else:
                        dm.update({key: next(filter(None, [value.get('value'), value.get('displayLabel')]))})
            if row.get('metricValues'):
                for key, value in row.get('metricValues').items():
                    dm.update({key: next(filter(None, [value.get('microsValue'),
                                                       value.get('integerValue'), value.get('doubleValue')]))})
            result.append(dm)
    return result


def to_csv(raw_report, output):
    report = to_plain_view(raw_report)

    dict_writer = csv.DictWriter(output, report[0].keys())
    dict_writer.writeheader()
    dict_writer.writerows(report)
